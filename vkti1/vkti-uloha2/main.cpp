#include <stdio.h>
#include <vector>
#include <list>
#include <queue>
#include <set>
#include <iostream>

using namespace std;

class Node;

typedef pair<int, int> Edge; 
typedef list<Node*>::iterator NodeIter;

class Node{
public:
    int Index;
    list<Node*> Neighbours;
    Node* Prev;
public:
    Node(int index):Index(index), Prev(NULL) {}
};

class Graph{
public:
    vector<Node*> nodes;
public:
    Graph(int nodeSize, const vector<Edge>& edges){
        nodes.reserve( nodeSize );
        for (int i = 0 ; i < nodeSize; i++){
            nodes.push_back(new Node(i));
        }
        
        for (unsigned int i = 0 ; i < edges.size(); i++){
            //cout << "edge["<<i<<"] = " << "["<<edges[i].first<<","<<edges[i].second<<"]" << endl;
            nodes[edges[i].first]->Neighbours.push_back( nodes[edges[i].second] );
            nodes[edges[i].second]->Neighbours.push_back( nodes[edges[i].first] );
        }
    }
    
    ~Graph(){
        for (unsigned int i = 0 ; i < nodes.size(); i++){
            delete nodes[i];
        }
    }
    
    //vyplni vsetky uzly vzdialenostami od start
    //tie potom treba prejst v cykle v case O(N)
    int BreadthFirstSearch( Node* start, Node* finish ){

        queue<Node*> q;
        q.push(start);
        
        set<Node*> visited;
        visited.insert(start);
        
        bool finishFound = false;
        while ( q.size() > 0 ){
            Node * t = q.front();
            q.pop();
            //cout << "I'm in " << t->Index << endl;
            if (t == finish){
                finishFound = true;
                break;
            }
            
            for (NodeIter it = t->Neighbours.begin(); it != t->Neighbours.end(); it++){
                Node * u = *it;
                if ( visited.count(u) == 0 ){
                    //u->Distance = t->Distance + 1;
                    u->Prev = t;
                    visited.insert(u);
                    q.push(u);
                    
                }
            }
        }
        if (!finishFound){
            return -1;
        }else{
            int count = 0;
            Node* current = finish;
            while (current != start){
                current = current->Prev;
                count++;
            }
            return count;
        }
    }
    
    int findMaximumShorthestPath(){
        int max = -1;
        for (unsigned int i = 0 ; i < nodes.size(); i++ ){
            for (unsigned int j = i ; j < nodes.size(); j++ ){
                if (i != j){
                    int d = BreadthFirstSearch(nodes[i], nodes[j]);
                    if (d > max){
                        max = d;
                    }
                }
            }
        }
        return max;
    }
    
    /*string toString(){
        string result = "";
        for (int i = 0 ; i < nodes.size(); i++){
        }
        return result;
    }*/
};

int main(int argc, char **argv)
{   
    int n , e ;
    cin >> n >> e;
 
    vector<Edge> edges;
    edges.reserve(e);
    for (int i = 0 ; i < e ; i++){
        int v1, v2;
        cin >> v1 >> v2;
        edges.push_back( make_pair(v1, v2) );
    }
    
    //int n = 5;
    //Edge edgesArray[] = { make_pair(0,1), make_pair(1,2),make_pair(2,3),make_pair(3,4), make_pair(4,0)};
    //vector<Edge> edges(edgesArray, edgesArray + 5);
    Graph graf(n, edges );
    
    int result = graf.findMaximumShorthestPath();
    cout << result << endl;
    return 0;
}
