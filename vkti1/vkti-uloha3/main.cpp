#include <stdio.h>
#include <vector>
#include <list>
#include <queue>
#include <set>
#include <iostream>
#include <sstream>

using namespace std;

vector<bool> getBinaryNumber(int c){
    vector<bool> tmp,result;
    int size = sizeof(c)*8;
    tmp.reserve(size);
    for (int i = 0 ; i < size; i++){
        tmp.push_back(c & 1);
        c = c >> 1;
    }
    bool firstOne = false;
    for (int i = tmp.size() - 1; i >=0 ; i-- ){
        if ( !firstOne && tmp[i] ){
            firstOne = true;
        }
        
        if (firstOne){
            result.push_back(tmp[i]);
        }
    }
    return result;
}



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
    string toString(){
        stringstream strStream;
        strStream << "["<<this->Index<<"]:[";
        for ( NodeIter it = Neighbours.begin(); it != Neighbours.end(); it++){
            Node * n = *it;
            if (it != Neighbours.begin()){
                strStream << ",";
            }
            strStream << n->Index;
        }
        strStream << "]";
        return strStream.str();
    }
    
    void toString2(){
        for ( NodeIter it = Neighbours.begin(); it != Neighbours.end(); it++){
            Node * n = *it;
            cout << this->Index << " " << n->Index << endl;
        }
    }
};

class Graph{
public:
    vector<Node*> nodes;
    int edgeCount;
public:
    Graph(int c):edgeCount(0){
        vector<bool> binary = getBinaryNumber(c);
        nodes.reserve( binary.size()*2 + 2 );
        nodes.push_back(new Node(0));
        
        //pretoze x^0
        unsigned int s = binary.size() - 1 ;
        for (unsigned int i = 1 ; i <= s*2; i+=2){
            nodes.push_back(new Node(i));
            nodes.push_back(new Node(i+1));
            if (i > 1){
                //1-->3, 2-->4 , i == 3
                nodes[i-2]->Neighbours.push_back(nodes[i]);
                nodes[i-1]->Neighbours.push_back(nodes[i+1]);
                //1-->4, 2-->3 , i == 3
                nodes[i-2]->Neighbours.push_back(nodes[i+1]);
                nodes[i-1]->Neighbours.push_back(nodes[i]);
                
                edgeCount += 4;
            }
        }
        nodes.push_back(new Node(nodes.size()));
        if ( binary.size() > 1){
            nodes[ nodes.size() - 2]->Neighbours.push_back(nodes[nodes.size() - 1]);
            nodes[ nodes.size() - 3]->Neighbours.push_back(nodes[nodes.size() - 1]);
            edgeCount += 2;
        }
        for (unsigned int i = 0; i < binary.size(); i++){
            if ( binary[i]){
                if (i == binary.size() - 1){
                    nodes[0]->Neighbours.push_back( nodes[nodes.size() - 1] );
                    edgeCount++;
                }else{
                    int index1 = i*2+1;
                    int index2 = i*2+2;
                    nodes[0]->Neighbours.push_back( nodes[index1] );
                    nodes[0]->Neighbours.push_back( nodes[index2] );
                    edgeCount+=2;
                }
            }
        }
    }
    
    ~Graph(){
        for (unsigned int i = 0 ; i < nodes.size(); i++){
            delete nodes[i];
        }
    }
    
    int countPaths(Node * current, Node * end){
        //cout << "I'm in " << current->Index ;
        if (current == end){
            //cout << " and in finish" << endl;
            return 1;
        }
        //cout << endl;
        int result = 0;
        for (NodeIter it = current->Neighbours.begin(); it != current->Neighbours.end(); it++){
            Node * child = *it;
            result = result + countPaths(child, end);
        }
        return result;	
    }
    
    string toString(){
        stringstream result;
        for (unsigned int i = 0 ; i < nodes.size(); i++){
            result << nodes[i]->toString() << endl;
        }
        return result.str();
    }
    
    void toString2(){
        cout << nodes.size() << " " << edgeCount << endl;
        for (unsigned int i = 0 ; i < nodes.size(); i++){
            nodes[i]->toString2();
        }
    }
};


void showBinary(int c){
    vector<bool> binary = getBinaryNumber(c);
    for (unsigned int i = 0; i < binary.size(); i++){
        if (binary[i]){
            cout << "1";
        }else{
            cout << "0";
        }
    }
    cout << endl;
}

int main(int argc, char **argv)
{   
    stringstream str;

    str << "asda" << 5 << "asdas";
    int c = 5;
    Graph graf(c);
    graf.toString2();
    //int paths = graf.countPaths( graf.nodes[0], graf.nodes[ graf.nodes.size() - 1 ]) ;
    //cout << graf.countPaths( graf.nodes[0], graf.nodes[ graf.nodes.size() - 1 ]) << endl;
    return 0;
}
