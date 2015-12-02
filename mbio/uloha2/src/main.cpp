#include <iostream>
#include <vector>
#include <fstream>
#include <memory>
#include <string>
#include <map>
#include <stdexcept> 
#include <sstream>
#include <algorithm>
#include <limits>
#include <stdlib.h>
#include <math.h>
#include <tuple>    
    
using namespace std;

const string BASES = "ACTG";


//Len pomocne funkcie
class Utils{
public:
    static vector<string> split(string data, const string& token) {
        vector<string> output;
        size_t pos = string::npos;
        do {
            pos = data.find(token);
            output.push_back(data.substr(0, pos));
            if (string::npos != pos) {
                data = data.substr(pos + token.size());
            }
        } while (string::npos != pos);
        return output;
    }
    
    static vector<string> readAllLines(const string& fileName) {
        vector<string> result;
        ifstream file (fileName);
        if (file.is_open()){
            string line;
            while ( getline (file,line) ){
                if (!line.empty()) {
                    result.push_back(line);
                }
            }
        }
        file.close();
        return result;
    }
    
    static string trim(string str, const string& trimString = " \n\r\t" ) {
        size_t end = str.find_last_not_of( trimString );
        if ( end != std::string::npos )
            str.resize( end + 1 );

        size_t start = str.find_first_not_of( trimString );
        if ( start != std::string::npos )
            str = str.substr( start );

        return std::move( str );;
    }
    
    //Dostan vektor v octavovom tvare
    static string getOctaveVector(const vector<double>& x) {
        stringstream code; code << "[";
        for ( unsigned int i = 0; i < x.size(); i++ ) {
            if ( i > 0 ) {
                code << ",";
            }
            code << x[i];
        }
        code << "]";
        return code.str();
    } 
    
    
    //Vyplotuj histogram do obrazky
    static void octaveHist(const vector<double>& x, const string& outPng){
        stringstream cmd;cmd<< "octave --eval \"";
        cmd << "bar("<<getOctaveVector(x)<<"); \n";
        cmd << "print -dpng " << outPng;
        cmd << "\" /dev/null 2>&1";
        cout << "Plotting histogram to : "<< outPng << endl;
        system(cmd.str().c_str());
    }
    
    static vector<int> getRandomIndexes(int count, int arrSize ) {
        vector<int> result;
        result.reserve(arrSize);
        for (unsigned int i = 0 ; i <  arrSize; i++) {
            result.push_back( i );
        }
        std::random_shuffle(result.begin(), result.end());
        return vector<int>(result.begin(), result.begin() + count);
    }
    
};

class Node; // forward decl
typedef shared_ptr<Node> NodePtr;

class Node {
public:
    string name;
    double edgeWeight;
    NodePtr parent;
    NodePtr left;
    NodePtr right;
    char base;
    map<char, double> memory;
public:
    Node(const string& name, double edgeWeight) :
        name(name),
        edgeWeight(edgeWeight),
        parent(NULL),
        left(NULL),
        right(NULL)
    {}

    bool isLeaf(){
        return !left && !right;
    }
    
    /* A[v,a] = sum(A[y,b]*P(b,a,ty))*sum(A[z,c]*P(c,a,tz)) s memorizaciou!! */
    double A(char a, double alfa) {
        if (memory.find(a) == memory.end() ){
            double result = 0;
            if (isLeaf()) {
                if (base == '-' || base == a || base == 'N') {
                    return 1; 
                } else {
                    return 0; 
                }
            } else {    
                double sum1 = 0;
                for (char b : BASES) { 
                    sum1 += left->A(b, alfa)*left->P(b,a,alfa);
                }
                
                double sum2 = 0;
                for (char c : BASES) {
                    sum2 += right->A(c,alfa)*right->P(c,a,alfa);
                }
                result = sum1 * sum2; 
            }
            memory[a] = result;
            return result;
        } else {
            return memory[a];
        }
    }
    
    
    /* Jukes-Cantorov model */
    double P(char b, char a, double alfa) {
        double sign = (b == a ? 3.0 : -1.0);
        return (1.0 + sign*exp((-4.0/3.0)*alfa*edgeWeight)) / 4.0;
    }
    
    void addChild(NodePtr child) {
        if (!left) {
            left = child;
        } else if (!right) {
            right = child;
        } else {
            throw logic_error("Adding third child to " + child->name + string(" ") + this->name);
        }
    }
    
    string toString() {
        stringstream strStream;
        strStream << "[ "<<this->name<<" : "<<(left?left->name:"NULL" )<<", "<<(right?right->name:"NULL" )<<"]";
        return strStream.str();
    }
};


typedef map<string,string>  SequenceMap;
typedef vector<string>       Window;
typedef vector<Window>       Windows;

class PhyloTree {
public:
    vector<NodePtr> nodes;
    map<string, NodePtr> nameToPtr;
    NodePtr root;
public:
    PhyloTree(const string& fileName) {
        loadFromFile(fileName);
    }
    
    /* felstenstein, qa = 1/4 je nase, tu sa zacina rekurzia!!
     * 
     * alfa - rychlost evoluciem, 
     * aligment - stlpec zarovnania
     */
    double felstenstein(double alpha, const string& alignmentColumn) {
        if (alignmentColumn.size() != getLeafCount()) {
            throw invalid_argument("Invalid alignmentColumn size");
        }
        for (NodePtr node : nodes ) {
            node->memory.clear();
        }
        setLeafBases(alignmentColumn);
        double result = 0;
        for ( char a : BASES ){
            double tmp = root->A(a, alpha);
            result += tmp * (1.0/4.0) ;
        }   
        return result;
    }
    /* Dostane vsetky listy stromu */
    vector<NodePtr> getLeafs() {
        vector<NodePtr> result;
        for (NodePtr node : nodes) {
            if ( node->isLeaf() ) {
                result.push_back(node);
            }
        }
        return result;
    }
    
    /* pri felstensteine nastav listom bazy zo stlpca zarovnania */
    void setLeafBases(const string& alignmentColumn) {
        vector<NodePtr> leafs = getLeafs();
        for (unsigned int i = 0; i < leafs.size(); i++) {
            leafs[i]->base = alignmentColumn[i];
        }
    }
    
    /* dostan pocet listo v strome */
    int getLeafCount() {
        int result = 0;
        for (NodePtr node : nodes) {
            if ( node->isLeaf() ) {
                result++;
            }
        }
        return result;
    }
    
    /* nacitaj strom zo suboru strom.txt */
    void loadFromFile(const string& fileName) {
        nodes.clear();
        nameToPtr.clear();
        root.reset();
        root = make_shared<Node>("Root", 0);
        nodes.push_back(root);
        nameToPtr.insert(make_pair(root->name, root));
        
        vector<string> lines = Utils::readAllLines(fileName);
        
        for ( const string& line : lines ) {
            vector<string> linesSplitted = Utils::split(line, " ");
            if (linesSplitted.size() == 3) {
                string name   = linesSplitted[0];
                string parentName = linesSplitted[1];
                double weight = stod(linesSplitted[2]);
                
                NodePtr node = make_shared<Node>(name,  weight);
                nameToPtr[parentName]->addChild(node);
                
                nodes.push_back(node);
                nameToPtr.insert(make_pair(node->name, node));
                
            }else {
                throw logic_error(string("Invalid line ") + line);
            }
        }
    }
    
    /* nacita subor zarovnani do map<organizmus,seqvencia>, 
     * aby sme vedeli pristupovat k sekvenciam organizmov */
    SequenceMap loadCftr(const string& fileName) {
        SequenceMap result;
        vector<string> lines = Utils::readAllLines(fileName);
        stringstream strStream;
        string currentOrganism;
        for ( string line : lines ) {
            if ( line[0] == '>') {
                if (!currentOrganism.empty()) {
                    result.insert(make_pair(currentOrganism, strStream.str()));
                }
                currentOrganism = Utils::trim(Utils::trim(line), ">");
                strStream.str("");
            } else{
                strStream << Utils::trim(line);
            }
        }
        result.insert(make_pair(currentOrganism, strStream.str()));
        return result;
    }
    
    /* vrati definovany stlpec zarovnania */
    string getAligmentColumn(const SequenceMap& cftr, int column){
        string result;
        vector<NodePtr> leafs = getLeafs();
        for (NodePtr node : leafs) {
            result.push_back(cftr.at(node->name)[column]);
        }
        return result;
    }
    
    /* vrati count nahodnych stlpcov zarovnania, pouzival som len na testovanie*/
    Window getRandomAligments(const SequenceMap& cftr, int count) {
        vector<int> indexes = Utils::getRandomIndexes(count, cftr.begin()->second.size());
        Window result;
        for ( int column : indexes ) {
            result.push_back(getAligmentColumn(cftr, column));
        }
        return result;
    }
    
    /* Vrati (end - start) stlpcov zarovnani teda okno velkost |end-start|. 
     * Myslim, ze je to jasne*/
    Window getWindow(const SequenceMap& cftr, int startIndex, int endIndex) {
        Window result;
        if ( endIndex > cftr.begin()->second.size() ) {
            stringstream strStream;strStream << "Out of range : max size = " << cftr.begin()->second.size() << ", endIndex = " << endIndex;
            throw out_of_range(strStream.str());
        }
        for ( int i = startIndex; i < endIndex; i++ ) {
            result.push_back(getAligmentColumn(cftr, i));
        }
        
        return result;
    }
    
    /* Vrati vsetky okna z mapy, windowSize definuje pocet stlpcov v okne */
    Windows getWindows(const SequenceMap& cftr, int windowSize) {
        int count = cftr.begin()->second.size() / windowSize;
        return getWindows(cftr, count, windowSize);
    }
    
    /* Vrati len count pocet okien*/
    Windows getWindows(const SequenceMap& cftr, int count, int windowSize) {
        Windows result;
        for (unsigned int i = 0; i < count; i++){
            result.push_back( getWindow(cftr, i*windowSize, (i+1)*windowSize ) );
        }
        return result;
    }
    
    /* Riesenie d.) prelezie vsetky alfy na <0.1,2> a zisti najlepsie alfa, ktore vrati*/
    double findOptimalAlpha(const Window& aligments) {
        double result = 0;
        double bestProb = -std::numeric_limits<double>::max();
        for ( double alpha = 0.1; alpha <= 2.0 ; alpha += 0.1 ) {
            double prob = 0;
            for ( string aligment : aligments ) {
                double fp = felstenstein(alpha, aligment);
                prob =+ log2(fp);
            }
            
            if ( prob > bestProb ) {
                bestProb = prob;
                result = alpha;
            }
        }
        return result;
    }
    
    /* Vrati vektor najlepsich alfa hodnot pre definovane okna*/
    vector<double> findOptimalAlphas(const vector<Window>& windows) {
        vector<double> result;
        for ( const Window& window : windows ) {
            result.push_back( findOptimalAlpha(window) );
        }
        return result;
    }
    
    /* pre AAA--TT--G  
     *     0123456789
     *  -> funkcia vrati
     *     012569
     *  -> Toto mozme vyuzit na rekonstrukciu povodnych pozicii exonov ( resp. pozicie v zarovnani )
     */
    vector<int> getOriginalPositions(const SequenceMap& cftr, const string& animal) {
        string sequence = cftr.at(animal);
        vector<int> result;
        int dash_count = 0;
        for (unsigned int i = 0 ; i < sequence.size(); i++) {
            if ( sequence[i] != '-') {
                result.push_back(i);
            } 
        }
        return result;
    }
    
    /* Nacita exon zo suboru ako. exon.begin je na i-tej pozici exon.end na i+1 */
    vector<int> loadExon(const string& str){
        vector<int> result;
        vector<string> lines = Utils::readAllLines(str);
        for (string line : lines ) {
            vector<string> splitted = Utils::split(line, " ");
            if (splitted.size() !=  2) {
                throw logic_error("Invalid line in exon file");
            }
            result.push_back( stoi( splitted[0] ) );
            result.push_back( stoi( splitted[1] ) );
        }
        return result;
    }
    
    /* upravi pozicie exonov podla zarovnania */
    vector<int> updateExons(const vector<int>& aligmentPos,  vector<int> exons ) {
        for ( int& exon : exons ) {
            if (exon > aligmentPos.size()) {
                throw out_of_range("Toto sa nemalo stat!!!");
            }
            exon = aligmentPos[exon];
        }
        return exons;
    }
    
    /* Rozdeli okna na tie, ktore maju koliziu s exonom a tie, ktore nemaju */
    tuple<Windows,Windows> splitByOverlap( const vector<int>& exons, const Windows& windows ) {
        Windows overlapped, notOverlapped;
        for (int i = 0; i < windows.size(); i++) {
            const Window& window = windows[i];
            int startIndex = i*window.size();
            int endIndex = (i+1)*window.size();
            bool overlap = false;
            for ( int i = 0 ; i < exons.size(); i+= 2 ) {
                
                int a1 = exons[i]; 
                int b1 = exons[i+1];
                int a2 = startIndex;
                int b2 = endIndex;
                if (startIndex < exons[i] ) {
                    swap(a1,a2);
                    swap(b1,b2);
                }
                
                if ( a1 <= a2 && a2 <= b1) {
                    overlap = true; 
                    break;
                }
            }
            
            if (overlap) {
                overlapped.push_back(window);
            } else { 
                notOverlapped.push_back(window);
            }
        }
        return make_tuple(overlapped, notOverlapped);
    }
};

class Uloha{
public:
    string stromPath;
    string cftrPath;
    string exonyPath;
    shared_ptr<PhyloTree> tree;
    SequenceMap cftr;
public: 
    Uloha(const string& stromPath, const string& cftrPath, const string& exonyPath) :
        stromPath(stromPath),
        cftrPath(cftrPath),
        exonyPath(exonyPath)
    {}

    int execute() {
        tree = shared_ptr<PhyloTree>( new PhyloTree(stromPath) );
        cftr = tree->loadCftr(cftrPath);
        ulohaB();
        //ulohaC();
        ulohaD();
        ulohaE();
    }
    
    void ulohaB() {
        string alignment = "CCCCCCCCCCCCCC";
        double prob1 = tree->felstenstein(1, alignment);
        double prob2 = tree->felstenstein(0.2, alignment);
        cout << "Uloha b.)" << endl;
        cout << "alfa = "<< 1 << " -> pravdepodobnost = " << prob1 << endl;
        cout << "alfa = "<< 0.2 << " -> pravdepodobnost = " << prob2 << endl;
        cout << "--------------------------------------------"<<endl;
    }
    
    //Toto mi sluzilo len na testovanie, nebolo to v zadani
    void ulohaC() {
        auto aligments = tree->getRandomAligments(cftr, 150);
        double bestAlpha = tree->findOptimalAlpha(aligments);
        cout << "Uloha c.)" << endl;
        cout << "best alpha from random sample = "<< bestAlpha << endl;
        cout << "--------------------------------------------"<<endl;
    }
    
    void ulohaD() {
        Windows windows = tree->getWindows(cftr, 10, 100);
        cout << "Uloha d.)" << endl;
        vector<double> alphas = tree->findOptimalAlphas(windows);
        for ( unsigned int i = 0; i < windows.size(); i++ ) {
            cout << "Window["<<i*windows[i].size()<<"-"<<(i+1)*windows[i].size()<<"].bestAlpha = " << alphas[i] << endl;
        }
        Utils::octaveHist(alphas, "all_alphas_hist.png");
        cout << "--------------------------------------------"<<endl;
    }
    
    void ulohaE() {

        vector<int> exons = tree->loadExon(exonyPath);
        vector<int> originalPositions = tree->getOriginalPositions(cftr,"Human");
        exons = tree->updateExons(originalPositions,exons);
        Windows windows = tree->getWindows(cftr, 100);
        Windows overlapped, notOverlapped;
        tie(overlapped,notOverlapped) = tree->splitByOverlap(exons, windows);
        
        
        //random_shuffle(overlapped.begin(), overlapped.end());
        //random_shuffle(notOverlapped.begin(), notOverlapped .end());
        
        overlapped = Windows(overlapped.begin(), overlapped.begin() + overlapped.size() );
        notOverlapped = Windows(notOverlapped.begin(), notOverlapped.begin() + overlapped.size() ); 
        
        vector<double> overlappedAlphas    = tree->findOptimalAlphas(overlapped);
        vector<double> notOverlappedAlphas = tree->findOptimalAlphas(notOverlapped);
        
        Utils::octaveHist(overlappedAlphas, "overlapped_alphas_hist.png");
        Utils::octaveHist(notOverlappedAlphas, "notOverlapped_alphas_hist.png");
    }
};

int main(int argc, char** argv) {
    try {        
        srand (time(NULL));
        return Uloha("strom.txt","cftr.txt","exony.txt").execute();
    }catch (exception& ex) {
        cerr << "Exception : " <<ex.what()<< endl;
    }
}