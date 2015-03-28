#include <stdio.h>
#include <vector>
#include <list>
#include <queue>
#include <set>
#include <iostream>
#include <string>
#include <sstream>
#include <limits>
#include <algorithm>
#include <stack>
#include <stdlib.h>

using namespace std;

#define POS_NODE(i) (i+1)

/******* Funkcie na permutacie *******/
class Utils{
private:
    static void swap(vector<int>& array, int x, int y){
        int temp = array[x];
        array[x]=array[y];
        array[y]=temp;

        return;
    }

    static void permuteRek(int k,int size, vector<int>& array, vector<vector<int> >& permutations){
        int i;

        if (k==0){
            permutations.push_back(vector<int>());
            permutations[permutations.size() - 1].reserve(size);
            for (i=0;i<size;i++){
                permutations[permutations.size() - 1].push_back( array[i] );
            }
        }
        else{
            for (i=k-1;i>=0;i--){
                swap(array, i,k-1);
                permuteRek(k-1,size, array, permutations);
                swap(array, i,k-1);
            }
        }
        return;
    }
public:
    static void permute(int k,int size, vector<vector<int> >& permutations ){
        vector<int> array;
        for (int i = 0 ; i < size; i++){
            array.push_back(i);
        }
        permuteRek(k,size, array, permutations);
    }

    static void printVector(vector<int>& perm){
        for (unsigned int i = 0; i < perm.size(); i++){
            if (i > 0) cout << " "; 
            cout << (perm[i] + 1 );
        }
        cout << endl;
    }

    static void printPermutations(vector<vector<int> >& permutations){
        for (unsigned int i = 0; i < permutations.size(); i++){
            for (unsigned int j = 0; j < permutations[i].size(); j++)
            {
                if (j > 0){
                    cout << " ";
                }
                cout << permutations[i][j];
            }
            cout << endl;
        }
    }
};


/********** Grafy *************/
class Node{
public:
    int index;
    vector<Node*> neighbours;
    Node * prev;
    int weight;
public:
    Node(int index):index(index),prev(NULL) {}
    string toString(){
        stringstream strStream;
        strStream << "["<<POS_NODE(index)<<"->";
        for (unsigned int i = 0; i < neighbours.size(); i++){
            if (i > 0) strStream << ", ";
            strStream << POS_NODE(neighbours[i]->index);
        }  
        strStream << "]";
        return strStream.str();
    }
    
    int degree(){
        return this->neighbours.size();
    }
};

struct Edge{
public:
    Node* u;
    Node* v;
    int cost;
public:
    Edge(Node* u, Node* v, int cost)
        :u(u), v(v), cost(cost)
    {}
    
    int isAdjacent(Edge* other ){
        return  this->u == other->v || this->v == other->v || 
                this->u == other->u || this->v == other->u ;
    }
    
    bool containsNodes(int n1, int n2){
        return  (u->index == n1 && v->index == n2) || 
                (u->index == n2 && v->index == n1) ;
    }
    
    
    friend bool operator==(const Edge& lhs, const Edge& rhs);
    friend class EdgeComparator;
    
    string toString(){
        stringstream strStream;
        strStream << "["<<POS_NODE(u->index)<<"->"<<POS_NODE(v->index)<<"] = " << cost; 
        return strStream.str();
    }
};


struct EdgeComparator{
    bool operator()( const Edge* lhs, const Edge* rhs){
        return lhs->cost < rhs->cost;
    }
};

struct NodeDegreeComparator{
    bool operator()( Node* lhs, Node* rhs){
        return lhs->neighbours.size() > rhs->neighbours.size();
    }
};

class Graph{
public:
    int n;
    vector<Edge*> edges;
    vector<Node*> nodes;
    vector<vector<int> > costs;
public:  

    void create(int size){
        this->clear();
        this->n = size;
        for (int i = 0; i < this->n; i++){
            nodes.push_back(new Node(i));
        }
    }
    Graph(){
    }
        
    Graph( vector<vector<int> >& costs){
        this->n = costs.size();
        this->edges.reserve( (n *( n - 1) ) / 2 );
        for (unsigned int i = 0; i < costs.size(); i++){
            nodes.push_back(new Node(i));
        }
        
        for (unsigned int i = 0; i < costs.size(); i++){
            for (unsigned int j = i ; j < costs[i].size(); j++){
                if (i != j){ 
                    addEdge( nodes[i], nodes[j], costs[i][j] );
                }
            }
        }
        this->costs = costs;
    }
    
    Edge *  addEdge(Node * u ,Node * v, int cost ){
        Edge * e = new Edge(u,v, cost ); 
        edges.push_back( e );
        u->neighbours.push_back( v );
        v->neighbours.push_back( u );
        return e;
    }
    
    bool dfs(Node * start, Node * end ){
        set<Node*> visited;
        stack<Node*> s;
        s.push(start);
        
        while ( s.size() > 0 ){
            Node * current = s.top();
            s.pop();
            if (current == end){
                return true;
            }
           
            for ( unsigned int i = 0; i < current->neighbours.size(); i++){
                Node * next = current->neighbours[i]; 
                if ( visited.count(next) == 0 ){
                    visited.insert(next);
                    s.push(next);
                }                
            }
        }
        return false;
    }
 
    int kruskal( Graph& minGraph ){
        int result = 0;
        minGraph.create(nodes.size());
        
        std::sort(edges.begin(), edges.end(), EdgeComparator() );
        set<Node*> nodesAdded;
        for (unsigned int i = 0 ; i < edges.size(); i++){
            Node * n1 = minGraph.nodes[ edges[i]->u->index ];
            Node * n2 = minGraph.nodes[ edges[i]->v->index ];
            bool foundPath = minGraph.dfs( n1, n2 ); 
            //foundPath = minGraph.dfs( n1, n2 ); 
            if ( !foundPath ){
                Edge * e = minGraph.addEdge( n1, n2 , edges[i]->cost );
                result+=e->cost;
                nodesAdded.insert(n1);
                nodesAdded.insert(n2);
                if (nodesAdded.size() == nodes.size()){
                    break;
                }
                //cout << "Added edge : "<< e->toString() << endl;
            }
            //cout << minGraph.toString();
            //cout << "-----------------"<<endl;
        }
        return result;
    }
    
    string toString(){
        stringstream strStream;
        strStream << "nodes: [";
        for (unsigned int i = 0 ; i < nodes.size(); i++){
            if (i > 0) strStream << ", ";
            strStream << nodes[i]->toString();
        }
        strStream << "]" << endl;
        strStream << "edges: [";
        for (unsigned int i = 0 ; i < edges.size(); i++){
            if (i > 0) strStream << ", ";
            strStream << edges[i]->toString();
        }
        strStream << "]";
        return strStream.str();
    }
    
    string toString2(){
        stringstream strStream;
        strStream << "{";
        for (unsigned int i = 0 ; i < nodes.size(); i++){
            if (i > 0) strStream << ", ";
            strStream << "{";
            for (unsigned int j = 0 ; j < nodes.size(); j++){
                if (j > 0) strStream << ", ";
                strStream << costs[i][j];
            }
            strStream << "}";
        }
        strStream << "}";
        return strStream.str();
    }
    
    int tsp(){
        int n = costs.size();
        int minCost = std::numeric_limits<int>::max();
        int minIndex = 0;
        
        vector<vector<int> > permutations;
        Utils::permute(n,n, permutations);
      
        for (unsigned int i = 0; i < permutations.size(); i++ ){
            //printPermutation(permutations[i]);
            int cost = 0; 
            for ( unsigned int j = 0; j < permutations[i].size(); j++  ){
                int u = j;
                int v = (j + 1) % permutations[i].size();
                cost += costs[ permutations[i][u] ][ permutations[i][v] ];
            }
            
            if (cost < minCost){
                minCost = cost;
                minIndex = i;
            }
        }
                
                
        //Utils::printVector(permutations[minIndex]);
        return minCost;
    }

    int getEdge(int u, int v){
        for (unsigned int i = 0; i < edges.size(); i++){
            if (edges[i]->containsNodes(u,v)){
                return i;
            }
        }
        return -1;
    }

    Node* findNode(int index){
        for (unsigned int i = 0; i < nodes.size(); i++ ){
            if (nodes[i]->index == index )
                return nodes[i];
        }
        return NULL;
    }

    //Volaj pre minimalnuKostru
    void generateNodeWeights(vector<int>& nodeWeigths){
        nodeWeigths.clear();
        nodeWeigths.resize(n);
        
        vector<Node*> nodesCopy = this->nodes;
        sort(nodesCopy.begin(), nodesCopy.end(), NodeDegreeComparator() );
        for ( int i = 0; i < nodesCopy.size(); i++ ){
            int index = nodesCopy[i]->index;
            if ( i < nodesCopy.size()/2 ){ 
                nodeWeigths[ index ] = 1; 
            }
            
            if ( i == nodesCopy.size() / 2){
                if (nodes.size() % 2 == 0){
                    nodeWeigths[ index ] = -1; 
                }else{
                    nodeWeigths[ index ] = 0; 
                }
            }
            
            if ( i > nodesCopy.size() / 2 ){ 
                nodeWeigths[ index] = -1; 
            }
        }
    }
    /*
    6 / 2 = 3
    0 1 2  3  4  5
    1 1 1 -1 -1 -1
    
    
    5 / 2 = 2    
    0 1 2 3 4
    1 1 0 -1 -1
    */
    
    void dfsFindWeigths( int positives, int negatives, int two){
        Node * start = NULL;
        for (unsigned int i = 0; i < nodes.size(); i++){
            nodes[i]->prev = NULL;
            nodes[i]->weight = 0;
            if ( start == NULL && start->degree() == 1){
                start = nodes[i];
            }
        }
        
        set<Node*> visited;
        stack<Node*> s;
        s.push(start);
        while ( s.size() > 0 ){
            Node * current = s.top();
            s.pop();
            /*if (current->prev == NULL){
                current->weight = -1;
                negatives--;
            }else if (current->prev->weight < 0){
                if ( (current->degree() > 2 || positives == 0) &&  two > 0 ) {
                    current->weight = 2;
                    two--;
                }else{
                    current->weight = 1;
                    positives--;
                }
            }else if (current->prev->weight > 0){
                if ()
            }*/
            
            for ( unsigned int i = 0; i < current->neighbours.size(); i++){
                Node * next = current->neighbours[i];
                next->prev = current;
                if ( visited.count(next) == 0 ){
                    visited.insert(next);
                    s.push(next);
                }                
            }
        }
    }

    void updateCosts(vector<int>& nodeWeigths){
        int n =  costs.size();
        for (int i = 0 ; i < n; i++){
            for (int j = i ; j < n; j++){
                if (i != j )
                {
                    costs[i][j] = nodeWeigths[i] + costs[i][j] + nodeWeigths[j];
                    costs[j][i] = costs[i][j];
                    Edge * e = edges[ getEdge(i,j) ];
                    e->cost = costs[i][j];
                    //cout << "updating " + e->toString()+" to " << costs[i][j] << endl;
                }
            }
        }
    }
    
    void clear(){
        for (unsigned int i = 0; i < edges.size(); i++ ){
            delete edges[i];
        }
        for (unsigned int i = 0; i < nodes.size(); i++ ){
            delete nodes[i];
        }
        this->costs.clear();
        this->nodes.clear();
        this->edges.clear();
        this->n = 0;
    }
    
    void testUloha(){
        
        cout << this->toString2() << "  -  ";
        
        Graph spanningTree;
        int st1  = this->kruskal(spanningTree);
        cout << spanningTree.toString() << endl;
        int tsp1 = this->tsp();
        
        vector<int> updateVec;
        spanningTree.generateNodeWeights(updateVec);
        //updateVec.push_back(2);
        //updateVec.push_back(-1);
        //updateVec.push_back(-1);
        //updateVec.push_back(-1);
        //updateVec.push_back(1);

        
        
        this->updateCosts(updateVec);
        
        int st2  = this->kruskal(spanningTree); 
        int tsp2 = this->tsp();
        
        if ( st2 > st1 && tsp1 == tsp2) {
            cout << "OK" << endl;
        }else{
            cout << "FAIL" << endl;
        }
    }
};


vector<vector<int> > generateRandomCosts(int n, int min, int max){
    vector<vector<int> > result;
    for (int i = 0; i < n ; i++){
        result.push_back(vector<int>());
        result[i].resize(n);
    }
    
    for (int i = 0; i < n ; i++){
        for (int j = 0; j< n ; j++){
            if (i == j){
                result[i][j] = 0;
            }else{
                int r = rand() % max + min;
                result[i][j] = r;
                result[j][i] = r;
            }
        }
    }
    return result;
}

void batchTest(){
    for (int i = 0; i < 100; i++){
         vector<vector<int> > costs = generateRandomCosts(5,1,10);
         Graph g(costs);
         g.testUloha();
    }
}
/************** MAIN **************/


int main(int argc, char **argv)
{   
    /*vector<vector<int> > costs;
    int n = 5;
    //int acost[5][5] = {{0, 3, 8, 3, 4}, {3, 0, 6, 9, 7}, {8, 6, 0, 10, 2}, {3, 9, 10, 0, 3}, {4, 7, 2, 3, 0}};
    int acost[5][5] = {{0,7,8,10,1},{7,0,8,8,5},{8,8,0,10,10},{10,8,10,0,7},{1,5,10,7,0}};
    for (int i = 0; i < n; i++){
        vector<int> vec( acost[i], acost[i] + sizeof(acost[i])/sizeof(int) );
        costs.push_back(vec);
    }
    Graph g(costs);
    g.testUloha();*/
    //batchTest();
    //return 0;
    
    
    vector<vector<int> > costs;
    int n = 0;
    cin >> n;
    for (int i = 0; i < n; i++){
        costs.push_back(vector<int>());
        for (int j = 0; j < n; j++){
            int c;
            cin >> c;
            costs[i].push_back(c);
        }
    }
    
    Graph g(costs);
    Graph spanningTree;
    g.kruskal(spanningTree);
    vector<int> updateVec;
    spanningTree.generateNodeWeights(updateVec);
    
    stringstream strStream;
    for (int i = 0; i < updateVec.size(); i++){
        if (i > 0) strStream << " ";
        strStream << updateVec[i];
    }
    
    string str = strStream.str();
    cout << str;
    return 0;
}
