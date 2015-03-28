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



using namespace std;

class Test
{
    static string boolToStr(bool b){
        return b ? "true" : "false";
    }
public:
    static bool areEqual(bool actual , bool expected, string testName){
        if (actual == expected){
            printf(" Test : %10s - OK\n", testName.c_str() );
            return true;
        }else{
            printf(" Test : %10s - FAILED, expected  %s, but was %s %\n", testName.c_str(), boolToStr(expected).c_str(), boolToStr(actual).c_str() );
            return false;
        }
    }
    
    static bool areEqual(int actual , int expected, string testName){
        if (actual == expected){
            printf(" Test : %10s - OK\n", testName.c_str() );
            return true;
        }else{
            printf(" Test : %10s - FAILED, expected  %d, but was %d %\n", testName.c_str(), expected, actual);
            return false;
        }
    }
};

////////////////////////////////////////////////////////////////////

const int SQUARE_SIZE = 2;
class Square{
public:
    int x, y ;
public: 
    Square(int x, int y)
        :x(x), y(y)
    {}
};

/*

void batchTest(int batchCount, int xMax, int yMax, int squareCount){
    srand(time(NULL));
    
    for (int b = 0; b < batchCount; b++){
        vector<Square> squares;
        squares.reserve(squareCount);
        for (int i = 0 ; i < squareCount; i++){
            int randX = (rand() % xMax);
            int randY = (rand() % yMax);
            squares.push_back(Square(randX, randY));
        }
        //Graph g(squares);
        stringstream testName;
        testName << "BatchTest-" << b;

        //bool r = Test::areEqual(g.getMaximumIndepenentSetCount(), g.bruteForce(), testName.str() );
        //if (r == false){
        //    cout << "--------------------------" << endl;
        //    cout << g.toString() << endl;
        //    cout << "--------------------------" << endl;
        //}
    }
}  
  

void testSquares(){
    Test::areEqual(true , Square(1,1).overlap(Square(1,1)) , "TestSquares1");
    Test::areEqual(false, Square(3,3).overlap(Square(1,1)) , "TestSquares2");
    
    Test::areEqual(true, Square(2,2).overlap(Square(1,1)) , "TestSquares3");
    Test::areEqual(true, Square(1,1).overlap(Square(2,2)) , "TestSquares4");
    
    Test::areEqual(true, Square(0,0).overlap(Square(1,1)) , "TestSquares5");
    Test::areEqual(true, Square(2,0).overlap(Square(1,1)) , "TestSquares6");
    Test::areEqual(true, Square(0,2).overlap(Square(1,1)) , "TestSquares7");
    Test::areEqual(true, Square(2,2).overlap(Square(1,1)) , "TestSquares8");
    
    Test::areEqual(true, Square(1,1).overlap(Square(0,0)) , "TestSquares9");
    Test::areEqual(true, Square(1,1).overlap(Square(2,0)) , "TestSquares10");
    Test::areEqual(true, Square(1,1).overlap(Square(0,2)) , "TestSquares11");
    Test::areEqual(true, Square(1,1).overlap(Square(2,2)) , "TestSquares12");
    
    Test::areEqual(false, Square(2,0).overlap(Square(0,2)) , "TestSquares12");
    Test::areEqual(false, Square(0,2).overlap(Square(2,0)) , "TestSquares13");
    
    //Test::areEqual(true, Square(2,2) == Square(2,2)  , "TestSquares14");
    //Test::areEqual(false, Square(1,2) == Square(2,2) , "TestSquares15");
    
    Test::areEqual(true, Square(1,0).overlap(Square(0,0)) , "TestSquares15");
    //Test::areEqual(false, Square(0,2).overlap(Square(2,0)) , "TestSquares16");
    
    set<Square> squareSet;
    squareSet.insert(Square(0,2));
    squareSet.insert(Square(0,2));
    Test::areEqual(squareSet.size() , 1 , "TestSquares16");
}
*/

int uloha(const vector<Square>& squares);

void testUloha(){
    
    vector<Square> sqList;
    /*
    sqList.clear();
    sqList.push_back(Square(2, 0));
    sqList.push_back(Square(2, 1));
    sqList.push_back(Square(0, 1));
    sqList.push_back(Square(2, 2));
    sqList.push_back(Square(1, 0));
    Test::areEqual( uloha(sqList), Graph(sqList).bruteForce(), "TestSquaresFail1");
    
    sqList.clear();
    sqList.push_back(Square(0, 2));
    sqList.push_back(Square(1, 2));
    sqList.push_back(Square(1, 1));
    sqList.push_back(Square(0, 1));
    sqList.push_back(Square(0, 2));
    Test::areEqual( uloha(sqList), Graph(sqList).bruteForce(), "TestSquares0");
    */
    sqList.clear();
    sqList.push_back(Square(0,0));
    sqList.push_back(Square(1,1));
    sqList.push_back(Square(2,2));
    Test::areEqual( uloha(sqList), 2 , "TestSquares1");
    
    sqList.clear();
    sqList.push_back(Square(0, 1));
    sqList.push_back(Square(1, 0));
    sqList.push_back(Square(1, 2));
    Test::areEqual( uloha(sqList), 2, "TestSquares2");

    sqList.clear();
    sqList.push_back(Square(1, 2));
    sqList.push_back(Square(1, 0));
    sqList.push_back(Square(0, 1));
    Test::areEqual( uloha(sqList), 2, "TestSquares3");

    sqList.clear();
    sqList.push_back(Square(0, 0));
    sqList.push_back(Square(0, 2));
    Test::areEqual( uloha(sqList), 2, "TestSquares4");

    sqList.clear();
    sqList.push_back(Square(0, 0));
    sqList.push_back(Square(0, 1));
    sqList.push_back(Square(0, 2));
    sqList.push_back(Square(1, 0));
    sqList.push_back(Square(1, 1));
    sqList.push_back(Square(1, 2));
    sqList.push_back(Square(2, 0));
    sqList.push_back(Square(2, 1));
    sqList.push_back(Square(2, 2));
    sqList.push_back(Square(3, 0));
    sqList.push_back(Square(3, 1));
    sqList.push_back(Square(3, 2));
    sqList.push_back(Square(4, 0));
    sqList.push_back(Square(4, 1));
    sqList.push_back(Square(4, 2));
    Test::areEqual( uloha(sqList), 6, "TestSquares5");
   
    sqList.clear();
    sqList.push_back(Square(0, 0));
    sqList.push_back(Square(0, 2));
    sqList.push_back(Square(1, 0));
    sqList.push_back(Square(1, 2));
    sqList.push_back(Square(2, 1));
    Test::areEqual( uloha(sqList), 3, "TestSquares6");
   
    sqList.clear();
    sqList.push_back(Square(0, 2));
    sqList.push_back(Square(1, 2));
    sqList.push_back(Square(1, 0));
    Test::areEqual( uloha(sqList), 2, "TestSquares7");
   
    sqList.clear();
    sqList.push_back(Square(0, 1));
    sqList.push_back(Square(1, 0));
    sqList.push_back(Square(1, 2));
    sqList.push_back(Square(2, 0));
    sqList.push_back(Square(2, 2));
    Test::areEqual( uloha(sqList), 3, "TestSquares8");
   
    sqList.clear();
    sqList.push_back(Square(0, 1));
    sqList.push_back(Square(1, 0));
    sqList.push_back(Square(1, 2));
    sqList.push_back(Square(2, 0));
    sqList.push_back(Square(2, 2));
    sqList.push_back(Square(3, 0));
    sqList.push_back(Square(3, 2));
    Test::areEqual( uloha(sqList), 4, "TestSquares9");
    
    sqList.clear();
    sqList.push_back(Square(2, 2));
    sqList.push_back(Square(2, 2));
    Test::areEqual( uloha(sqList), 1, "TestSquares10");
    
    sqList.clear();
    sqList.push_back(Square(0, 0));
    sqList.push_back(Square(1, 0));
    sqList.push_back(Square(0, 1));
    Test::areEqual( uloha(sqList), 1, "TestSquares11");
    

}


#define LARGE 1000000

int uloha(const vector<Square>& squares){
    vector<int> y0,y1,y2;
    for (unsigned int i = 0 ; i < squares.size(); i++){
        switch (squares[i].y ){
            case 0 : y0.push_back(squares[i].x); break;
            case 1 : y1.push_back(squares[i].x); break;
            case 2 : y2.push_back(squares[i].x); break;
        }
    }
    sort(y0.begin(), y0.end());
    sort(y1.begin(), y1.end());
    sort(y2.begin(), y2.end());
    
    int result = 0;
    int index0 = 0, index1 = 0, index2 = 0;
    int sX0 = -2, sX1 = -2, sX2 = -2; 
    
    for (unsigned int i = 0 ; i < squares.size();i++){
        int x0 = (index0 >= ((int)y0.size()) ) ? LARGE : y0[index0];
        int x1 = (index1 >= ((int)y1.size()) ) ? LARGE : y1[index1];
        int x2 = (index2 >= ((int)y2.size()) ) ? LARGE : y2[index2];
        
        
        if(x1 <= x0 && x1 <= x2){
            index1++;
            if(x1 >= sX0+2 && x1 >= sX1+2 && x1 >= sX2+2 && (x1 <= x2-2 || x1 <= x0-2) ) {
                sX1 = x1;
                result++;
            }
            continue;;;;;;;;;;;;
        }
        
        if(x2 <= x1 && x2 <= x0){
            index2++;	
            if(x2 >= sX2+2 && x2 >= sX1+2){
                sX2 = x2;
                result++;
            }
            continue;
        }
        
        if ( x0 <= x1  && x0 <= x2 ){
            index0++;
            if (x0 >= sX1+2  && x0 >= sX0+2){
                sX0 = x0;
                result++;
            }
            continue;
        }
    }
    return result;
}

/************** MAIN **************/

int main(int argc, char **argv)
{   
    //batchTest(100, 3, 3, 10);
    //testSquares();
    testUloha();
    
    int n = 0;
    cin >> n;
    vector<Square> squares;
    for (int i = 0; i < n; i++){
        int x, y;
        cin >> x >> y;
        squares.push_back(Square(x,y));
    }
    //Graph g(squares);
    
    cout << uloha(squares) << endl;

    return 0;
}
