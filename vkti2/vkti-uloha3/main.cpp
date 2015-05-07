#include <stdio.h>
#include <vector>
#include <set>
#include <iostream>
#include <string>
#include <sstream>
#include <stdlib.h>
#include <map>
#include <math.h>

#include <algorithm>
//#include <random>

using namespace std;

string getXstring(int i, int j){
    stringstream result;
    result << "x"<< (i+1)<<"_"<<(j+1);
    return result.str();
}

string getYstring(int i){
    stringstream result;
    result << "v_"<< (i+1);
    return result.str();
}

string generate_code(vector<vector<int> > matrix){
    stringstream strStream;
    strStream << "Minimize" << endl;
    strStream << "obj: ";
    bool beginLine = false;
    for ( unsigned int i = 0; i < matrix.size(); i++ ){
        for ( unsigned int j = 0; j < matrix.size(); j++ ){
            if (matrix[i][j] > 0){
                if ( beginLine ){strStream << " +"; }
                beginLine = true;
                strStream << matrix[i][j] <<" " << getXstring(i,j);
            }
        }
    }
    strStream << endl;
    /****************Riadky****************/
    strStream << "Subject To" << endl;
    for ( unsigned int i = 0; i < matrix.size(); i++ ){
        strStream << "ci"<<i+1<<": ";
        bool beginLine = false;
        for ( unsigned int j = 0; j < matrix.size(); j++ ){
            if ( matrix[i][j] ){
                if ( beginLine ){strStream << " + "; }
                beginLine = true;
                strStream << getXstring(i,j);
            }
        }
        strStream << " <= 1 "<<endl;
    }
    /****************Stlpce****************/
    for ( unsigned int i = 0; i < matrix.size(); i++ ){
        strStream << "co"<<i+1<<": ";
        bool beginLine = false;
        for ( unsigned int j = 0; j < matrix.size(); j++ ){
            if ( matrix[j][i] ){
                if ( beginLine ){strStream << " + "; }
                beginLine = true;
                strStream << getXstring(j,i);
            }
        }
        strStream << " <= 1 "<<endl;
    }
    /****************cx****************/
    
    strStream << "ce: ";
    beginLine = false;
    for ( unsigned int i = 0; i < matrix.size(); i++ ){
        for ( unsigned int j = 0; j < matrix.size(); j++ ){
            if ( matrix[j][i] ){
                if ( beginLine ){strStream << " + "; }
                beginLine = true;
                strStream << getXstring(i,j);
            }
        }
        
    }
    strStream << " = " << matrix.size() - 1 <<endl;

    for (unsigned int i = 0; i < matrix.size(); i++) {
        for (unsigned int j = 0; j < matrix.size(); j++) {
          if (i != j) {
            strStream << matrix.size()<<" "<<getXstring(i,j) << " + " <<  getYstring(i)<<" - "<< getYstring(j) << " <= " << matrix.size()-1 << endl;
          }
        }
        strStream << getYstring(i) << " >= 1" << endl;
        strStream << getYstring(i) << " <= " << matrix.size() << endl;
    }

    strStream << "Binary" << endl;
    beginLine = false;
    for ( unsigned int i = 0; i < matrix.size(); i++ ){
        for ( unsigned int j = 0; j < matrix.size(); j++ ){
            if ( matrix[i][j] > 0){
                 if ( beginLine ){strStream << " "; }
                 beginLine = true;
                 strStream << getXstring(i,j);
            }
        }
    }
    strStream << endl;
    strStream << "End";
    return strStream.str();
}

int main(int argc, char **argv)
{   
    vector<vector<int> > matrix;

   /* matrix.push_back(vector<int>());
    matrix.push_back(vector<int>());
    matrix.push_back(vector<int>());

    matrix[0].push_back(0);
    matrix[0].push_back(1);
    matrix[0].push_back(4);
    matrix[1].push_back(1);
    matrix[1].push_back(0);
    matrix[1].push_back(1);
    matrix[2].push_back(4);
    matrix[2].push_back(0);
    matrix[2].push_back(0);*/
    int n = 0;
    cin >> n;
    for (int i = 0; i < n; i++){
        matrix.push_back(vector<int>());
        for (int j = 0; j < n; j++){
            int tmp = 0;
            cin >> tmp;
            matrix[i].push_back(tmp);
        }
    }
    
    
    cout << generate_code(matrix) << endl; 
    return 0;
}
