#include <iostream>
#include <vector>
#include <fstream>
#include <memory>
#include <math.h>
#include <string>
#include <map>
#include <stdexcept> 
#include <sstream>
#include <algorithm>
#include <limits>

using namespace std;

const string BASES = "ACTG";

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
public:
    char base;
public:
    Node(const string& name, double edgeWeight) {
        this->name = name;
        this->edgeWeight = edgeWeight;
        this->parent = NodePtr(NULL);
        this->left   = NodePtr(NULL);
        this->right  = NodePtr(NULL);
    }

    bool isLeaf(){
        return !left && !right;
    }

    double A(char a, double alfa) {
        double result = 0;
        if (isLeaf()) {
            //cout << "I am in leaf " << toString() << endl;
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
        return result;
    }
    
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


typedef map<string,string >  AligmentMap;
typedef vector<string>       Window;
typedef vector<Window >      Windows;

class PhyloTree {
public:
    vector<NodePtr> nodes;
    map<string, NodePtr> nameToPtr;
    NodePtr root;
public:
    PhyloTree(const string& fileName){
        loadFromFile(fileName);
    }

    double felstenstein(double alpha, const string& alignment){
        if (alignment.size() != getLeafCount()) {
            throw invalid_argument("Invalid alignment size");
        }
        setLeafBases(alignment);
        double result = 0;
        for ( char a : BASES ){
            double tmp = root->A(a, alpha);
            result += tmp * (1.0/4.0) ;
        }   
        return result;
    }
    
    vector<NodePtr> getLeafs() {
        vector<NodePtr> result;
        for (NodePtr node : nodes) {
            if ( node->isLeaf() ) {
                result.push_back(node);
            }
        }
        return result;
    }
    
    void setLeafBases(const string& alignment) {
        vector<NodePtr> leafs = getLeafs();
        for (unsigned int i = 0; i < leafs.size(); i++) {
            leafs[i]->base = alignment[i];
        }
    }
    
    int getLeafCount(){
        int result = 0;
        for (NodePtr node : nodes) {
            if ( node->isLeaf() ) {
                result++;
            }
        }
        return result;
    }
    
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
    
    AligmentMap loadCftr(const string& fileName) {
        AligmentMap result;
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
    
    string getAligment(const AligmentMap& cftr, int column){
        string result;
        vector<NodePtr> leafs = getLeafs();
        for (NodePtr node : leafs) {
            result.push_back(cftr.at(node->name)[column]);
        }
        return result;
    }
    
    vector<int> getRandomIndexes(int count, int arrSize ) {
        vector<int> result;
        result.reserve(arrSize);
        for (unsigned int i = 0 ; i <  arrSize; i++) {
            result.push_back( i );
        }
        std::random_shuffle(result.begin(), result.end());
        return vector<int>(result.begin(), result.begin() + count);
    }
    
    Window getRandomAligments(const AligmentMap& cftr, int count) {
        vector<int> indexes = getRandomIndexes(count, cftr.begin()->second.size());
        Window result;
        for ( int column : indexes ) {
            result.push_back(getAligment(cftr, column));
        }
        return result;
    }
    
    Window getAligments(const AligmentMap& cftr, int startIndex, int count) {
        Window result;
        if ( startIndex + count > cftr.begin()->second.size() ) {
            stringstream strStream;strStream << "Out of range : max size = " << cftr.begin()->second.size() << ", actual size " << (startIndex + count);
            throw out_of_range(strStream.str());
        }
        for ( int i = startIndex ; i < count; i++ ) {
            result.push_back(getAligment(cftr, i));
        }
        return result;
    }
    
    Windows getWindows(const AligmentMap& cftr, int count, int windowSize) {
        Windows result;
        for (unsigned int i = 0; i < count; i++){
            result.push_back( getAligments(cftr, i*windowSize, i*(windowSize+1)) );
        }
        return result;
    }
    
    double findOptimalAlpha(const Window& aligments) {
        double result = 0;
        double bestProb = -std::numeric_limits<double>::max();
        for ( double alpha = 0.1; alpha <= 2.0 ; alpha += 0.1 ) {
            double prob = 1;
            for ( string aligment : aligments ) {
                double fp = felstenstein(alpha, aligment);
                prob =+ log10(fp);
            }
            
            if ( prob > bestProb ) {
                bestProb = prob;
                result = alpha;
            }
        }
        return result;
    }
};

int main(int argc, char** argv) {
    try {
        srand (time(NULL));
        
        PhyloTree tree("strom.txt");
        cout << tree.getLeafCount() << endl;
        //string alignment = "--------------";
        string alignment = "CCCCCCCCCCCCCC";
        double prob1 = tree.felstenstein(0.1, alignment);
        double prob2 = tree.felstenstein(0.2, alignment);
        cout << "Uloha b.)" << endl;
        cout << "alfa = "<< 0.1 << " -> pravdepodobnost = " << prob1 << endl;
        cout << "alfa = "<< 0.2 << " -> pravdepodobnost = " << prob2 << endl;
        cout << "--------------------------------------------"<<endl;
        
        auto cftr = tree.loadCftr("cftr.txt");
        /*auto aligments = tree.getRandomAligments(cftr, 500);
        double bestAlpha = tree.findOptimalAlpha(aligments);
        cout << "Uloha c.)" << endl;
        cout << "best alpha  = "<< bestAlpha << endl;
        cout << "--------------------------------------------"<<endl;*/
        
        cout << "Uloha d.)" << endl;
        int windowSize = 100;
        Windows windows = tree.getWindows(cftr, 10, windowSize);
        int i = 0;
        double average = 0;
        for ( Window& window : windows ) {
            double bestAlpha = tree.findOptimalAlpha(window);
            cout << " Window["<<i*windowSize<<"-"<<i*(windowSize+1)<<"].bestAlpha = " << bestAlpha << endl;
            average += bestAlpha;
            i++;
        }
        average /= windows.size();
        cout << " Window[Average].bestAlpha = " << average << endl;
        
    }catch (exception& ex) {
        cerr << "Exception : " <<ex.what() << endl;
    }
}