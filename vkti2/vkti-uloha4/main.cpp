#include <stdio.h>
#include <vector>
#include <set>
#include <iostream>
#include <string>
#include <sstream>
#include <stdlib.h>
#include <map>
#include <math.h>
#include <unordered_map>

using namespace std;

typedef long long int64;

#define PRIME 107
#define PRIME1 101
#define MODULO 2147483647
     

template <typename T>
T modpow(T base, T exp, T modulus) {
      base %= modulus;
      T result = 1;
      while (exp > 0) {
        if (exp & 1) result = (result * base) % modulus;
        base = (base * base) % modulus;
        exp >>= 1;
      }
      return result;
}         
              
class RollingHash
{
private:
    int64 k;
    int64 base;
    int64 modulo;
    int64 basePowed;
private:
    int64 lastPow;
public:
    RollingHash(int64 k , int64 modulo , int64 base)
        :k(k),base(base),modulo(modulo)
    {
        basePowed = mpow(base, k - 1);
        //computeHash(input);
    }
    
    int64 computeHash(int * input){
        int64 result = 0;
        for (int64 i = 0; i < k ; i++){
            int64 pow = mpow(base, k-i-1);
            int64 tmp = mmul(input[i], pow);
            result = madd(result, tmp);
        }
        return result;
    }

    inline int64 next(int64 old_num, int64 old_hash, int64 new_num){
        int64 old_a = mmul( old_num , basePowed );
        int64 result = msub( old_hash, old_a );
        result = mmul(result, base);
        result = madd(result, new_num);
        return result;
    }
    
    inline int64 mod(int64 n){ return n % modulo; }

    inline int64 madd (int64 a, int64 b){
        return mod(mod(a)+mod(b));
    }
    
    inline int64 msub (int64 a, int64 b){
        int64 res = mod(mod(a)-mod(b));
        if ( res < 0 ) {
            res = modulo + res;
        }
        return res;
    }
    
    inline int64 mmul (int64 a, int64 b){
        return mod(mod(a)*mod(b));
    }

    inline int64 mpow(int64 m, int64 powFactor){
        return modpow<int64>(m, powFactor, modulo);
    }
};


/*pair<int,int> execute(int * input, int n, int k){
    RollingHash rolledHash(input, k, MODULO, PRIME);
    unordered_map<int, int  > hashes;
    hashes.reserve( n - k + 1 );
    for (int i = 0; i < n - k + 1; i++){
        if ( hashes.count( rolledHash.current ) > 0){
            int prev = hashes[rolledHash.current];
            return make_pair( prev+1, i+1 );
        }
        hashes.insert( make_pair( rolledHash.current, i ) );
        if (i + k < n){
            rolledHash.next( input[i], input[i + k] );
        }
    }
    return make_pair(-1,-1);
}*/

inline bool areEqual(int * a, int * b , int k){
    for (int i = 0 ; i < k ; i++){
        if ( a[i] != b[i] ){
            return false;
        }
    }
    return true;
}

inline bool areEqual2(int * a, int * b , int k, int perCount){
    for (int i = 0 ; i < k ; i+=perCount){
        if ( a[i] != b[i] ){
            return false;
        }
    }
    return true;
}

pair<int,int> execute(int * input, int n, int k){
    RollingHash rolledHash(k, MODULO, PRIME);
    RollingHash rolledHash2(k, MODULO, PRIME1);
    
    unordered_map<int, vector<int> * > hashes;
    hashes.reserve( n*3 );
    hashes.rehash( n*3 );
 
    unordered_map<int, vector<int> * > hashes2;
    hashes2.reserve( n*3 );
    hashes2.rehash( n*3 );
 

   pair<int,int> result =  make_pair(-1,-1);
    
    int equalCheckCount = min(k,10);
    
    int rhash = 0;
    int rhash2 = 0;
    for (int i = 0; i < n - k + 1 ; i++){
        
        if (i == 0) { 
            rhash = rolledHash.computeHash(input+i);
            rhash2 = rolledHash2.computeHash(input+i);
        }else{
            rhash = rolledHash.next( input[i-1], rhash, input[i+k-1] );
            rhash2 = rolledHash2.next( input[i-1], rhash2, input[i+k-1] );
        }
        
        vector<int>* v = NULL;

        if ( hashes.count( rhash ) > 0 && hashes2.count(rhash2) > 0 ){
            v = hashes.at(rhash);
            for (unsigned int j = 0; j < v->size(); j++){
                int vj =  (*v)[j];
                int * a = input + vj;
                int * b = input + i;
                if ( areEqual2(a,b,k, max(1, k/10 ) ) ){                        
                    int vvj = vj+1;
                    int ii  = i+1;
                    if ( result.first == -1 || result.first > vvj || (result.first == vvj && result.second > ii ) ){
                        result =  make_pair( vvj,  ii );
                        (*v)[j] = i;
                    }
                }else{
                    v->push_back(i);
                }
            }
        }else{
            v = new vector<int>();
            hashes.insert( make_pair( rhash , v ) );
            v->push_back(i);
            
            hashes2.insert( make_pair( rhash2, v) );
            
        }
        
        //if (i % 10000 == 0){
            //cout << i << endl;
            //cout << v->size() << endl;
        //}
    }
    return result;
}



pair<int, int> executeNaive(int * input, int n, int k){
    for (int i = 0; i < n - k + 1; i++){
        for (int j = i + 1; j < n - k + 1; j++){
            int * a = input + i;
            int * b = input + j;
            if ( areEqual(a,b,k) ){
                return make_pair(i+1,j+1);
            }
        }
    }
    return make_pair(-1,-1);
}

////////////////////////////////////////////////////////////////////
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
            printf(" Test : %10s - FAILED, expected  %s, but was %s \n", testName.c_str(), boolToStr(expected).c_str(), boolToStr(actual).c_str() );
            return false;
        }
    }
    
    static bool areEqual(int actual , int expected, string testName){
        if (actual == expected){
            printf(" Test : %10s - OK\n", testName.c_str() );
            return true;
        }else{
            printf(" Test : %10s - FAILED, expected  %d, but was %d \n", testName.c_str(), expected, actual);
            return false;
        }
    }
    
    static bool areEqual(pair<int,int> actual , pair<int,int> expected, string testName){
        if (actual.first == expected.first && actual.second == expected.second ){
            printf(" Test : %10s - OK\n", testName.c_str() );
            return true;
        }else{
            printf(" Test : %10s - FAILED, expected  <%d,%d>, but was <%d,%d> \n", testName.c_str(), expected.first, expected.second, actual.first, actual.second);
            return false;
        }
    }
    


    static void testExecute(){
        int input1[] = {1,1,1,1,1};
        Test::areEqual( execute(input1,5,2), make_pair(1,2), "testExecute1" );

        int input2[] = {1,2,3,1,2};
        Test::areEqual( execute(input2,5,2), make_pair(1,4), "testExecute2" );

        int input3[] = {1,2,3,4,5};
        Test::areEqual( execute(input3,5,2), make_pair(-1,-1), "testExecute3" );
    }

    static void testNaive(){
        int input1[] = {1,1,1,1,1};
        Test::areEqual( executeNaive(input1,5,2), make_pair(1,2), "testNaive1" );

        int input2[] = {1,2,3,1,2};
        Test::areEqual( executeNaive(input2,5,2), make_pair(1,4), "testNaive2" );

        int input3[] = {1,2,3,4,5};
        Test::areEqual( executeNaive(input3,5,2), make_pair(-1,-1), "testNaive3" );

    }

    static void testBatch(int count, int n, int maxk){
        srand (time(NULL));
        int k = randomInt(1,maxk);
        for (int i = 0; i < count; i++){
            int * randArray = generateRandomArray(n, 0, 100);
            //cout << "k = " << k<< ", array = " << arrayToString(randArray,n) << endl;
            pair<int,int> naiveRes = executeNaive(randArray,n,k);
            pair<int,int> res = execute(randArray,n,k);
            
            stringstream testName; testName << "testBatch" << i; 
            bool ok = areEqual(res, naiveRes, testName.str() );
            if (!ok){
                cout << "k = " << k<< ", array = " << arrayToString(randArray,n) << endl;
            }
        }
    }
    
    static void testHash(){
        int seq1[] = {98,27,18};
        int seq2[] = {27,18,70};
        
        RollingHash rh(3, MODULO, PRIME);
        Test::areEqual(rh.computeHash(seq2) , 277315,"testHash1");
        
               
        int64 hash2 = rh.computeHash(seq1);
        hash2 = rh.next(98,hash2, 70);
        Test::areEqual((int)hash2, (int)rh.computeHash(seq2), "testHash2");
    }
    
    static void testSpecific(){
    
        int arr2[] = { 95,19,94,23,43,32,57,57,26,24,69,56,81,1,99,74,90,71,25,68,71,22,9,86,73,33,56,89,55,65,5,3,84,51,26,79,36,36,36,62,12,57,18,46,58,18,20,0,89,45,68,12,67,78,51,40,63,7,30,71,73,87,74,9,91,52,89,27,88,77,89,53,35,60,99,45,30,19,98,19,64,18,32,83,96,35,23,12,42,5,83,67,93,57,29,36,9,70,63,50,47,4,3,34,64,2,32,94,73,82,66,89,0,50,72,97,85,47,61,79,53,44,47,98,1,28,34,62,98,97,64,97,53,67,84,18,21,68,64,94,50,30,83,50,80,7,99,65,55,60,45,8,56,44,58,9,24,92,72,74,41,88,23,94,56,59,64,29,27,29,76,29,59,59,32,92,67,83,9,74,96,6,82,52,50,40,62,26,84,86,0,25,26,24,71,82,83,36,64,63,65,40,44,76,99,76,68,18,12,78,92,8,36,26,12,87,18,26,65,2,64,66,79,91,90,51,73,25,87,89,88,4,29,85,80,81,61,1,51,25,31,96,33,67,22,46,6,93,24,72,95,89,38,27,80,80,78,5,57,17,95,98,73,24,83,5,5,44,6,57,22,89,53,55,57,27,53,63,20,78,87,68,19,25,95,99,57,25,4,15,94,99,13,67,76,96,72,33,92,31,90,14,20,95,22,29,75,27,45,95,57,32,15,76,10,10,75,67,87,32,34,81,83,99,48,11,95,73,45,40,4,87,6,76,83,28,6,10,56,3,5,65,35,21,42,45,83,69,65,71,53,51,52,37,51,53,48,98,26,45,38,82,33,45,10,68,25,16,78,33,71,35,99,7,56,41,4,92,62,21,63,16,73,67,5,76,20,5,74,98,51,13,80,36,10,91,4,35,59,82,69,31,17,68,90,26,61,46,70,75,68,85,91,93,52,48,69,25,6,95,75,9,8,8,45,70,99,1,6,10,35,75,93,4,95,35,82,56,82,52,31,2,37,75,47,42,75,68,19,81,63,46,42,24,54,39,94,5,40,0,16,27,27,61,32,22,97,14,78,31,19,62,33,8,89,80,2,64,48,73,98,63,20,40,87,74,32,82,80,72,34,48,52,62,9,84,36,58,50,67,89,69,29,74,78,18,6,32,34,54,6,32,70,26,25,57,0,9,91,32,81,26,80,33,40,42,69,76,0,20,43,42,41,24,16,71,94,23,4,29,29,10,13,99,88,90,9,40,99,0,73,33,78,5,18,70,47,88,99,0,60,42,42,1,67,10,73,61,85,29,42,67,91,56,18,79,46,27,19,98,80,44,31,10,50,49,81,49,89,80,49,49,74,43,51,93,54,76,7,39,5,49,58,96,57,77,27,56,4,46,54,36,43,85,99,45,86,32,94,76,64,44,25,38,87,28,84,93,4,91,85,61,92,43,57,50,20,84,58,77,83,12,65,78,49,64,23,87,96,69,63,60,65,41,99,53,69,35,98,26,26,83,39,70,27,49,72,99,33,30,76,68,94,42,98,95,6,21,83,55,91,98,15,8,39,66,13,61,1,12,87,79,95,78,50,74,27,22,26,61,5,2,81,51,96,80,47,3,53,30,10,96,28,25,5,20,92,18,81,45,82,20,25,30,98,27,4,26,1,30,39,6,85,20,58,81,52,57,36,58,87,46,54,67,24,11,87,68,82,20,13,64,40,90,94,39,17,51,65,19,33,56,77,18,76,87,52,81,44,40,91,83,39,97,51,63,9,90,31,43,11,44,7,3,87,54,94,4,5,59,75,38,67,5,9,96,92,13,77,37,53,68,72,92,17,23,55,26,66,86,69,29,83,29,32,22,83,27,26,88,38,54,78,6,11,39,2,3,52,31,92,6,51,65,98,68,40,6,47,58,44,68,87,27,97,72,49,32,51,28,72,41,34,51,99,45,90,1,0,95,84,45,1,87,62,51,56,54,57,55,13,2,23,52,81,73,24,83,5,75,63,78,69,97,81,68,94,23,22,46,18,6,91,19,46,5,71,2,60,80,9,73,82,32,25,64,5,2,47,63,77,10,41,98,59,74,67,5,97,89,51,16,47,95,87,93,0,10,47,60,91,56,85,73,89,11,89,46,13,88,9,42,50,2,41,9,76,60,66,26,49,18,94,96,13,81,42,65,44,89,78,35,98,63,60,87,74,2,85,39,90,47,82,41,1,75,2,30,35,69,56,36,39,50,84,4,83,26,69,27,68,47,62,66,11,75,5,37,77,90};
        Test::areEqual( execute(arr2, 1000,4), executeNaive(arr2, 1000,4), "testSpecific1" );
        Test::areEqual( execute(arr2, 1000,1000), make_pair(-1,-1), "testSpecific2" );
        
        int arr3[] = { 4,17,71,9,74,42,93,19,2,36,31,67,10,1,13,92,56,57,93,1,55,99,78,12,75,51,85,85,80,22,95,36,92,18,98,66,13,91,37,15,80,68,35,90,21,48,82,30,57,76,31,64,27,61,77,2,64,62,39,97,85,87,33,77,5,83,95,18,75,32,86,7,52,21,97,73,21,32,3,79,60,86,43,87,0,72,41,16,35,80,13,72,67,99,1,25,82,48,95,9,32,81,68,84,54,66,9,76,50,65,7,10,3,2,97,3,75,38,20,62,70,85,86,90,36,87,67,19,35,62,80,19,96,49,3,50,67,64,78,69,29,37,79,33,40,28,88,67,18,60,81,88,46,67,30,82,54,97,53,41,60,86,60,8,87,15,58,54,79,89,23,61,78,54,46,70,82,34,37,52,95,18,92,93,85,23,27,91,72,33,32,32,19,44,92,6,11,51,12,91,92,87,4,70,41,50,93,75,36,30,27,83,1,19,76,38,42,56,30,15,89,14,99,8,11,92,66,22,95,78,65,39,65,21,61,58,71,54,33,8,37,12,43,90,31,20,28,26,76,10,93,65,77,92,25,88,36,91,62,83,21,80,74,38,1,36,96,25,42,81,85,31,93,28,21,24,0,2,2,76,12,95,93,41,40,70,81,28,13,44,12,34,76,38,24,77,26,20,2,69};
        Test::areEqual( execute(arr3, 300,2), executeNaive(arr3,300, 2), "testSpecific2" );
        
        int arr4[] = { 43,46,90,92,15,22,93,9,89,72,67,1,47,9,67,12,95,11,61,5,34,56,39,23,74,58,75,91,73,58,28,68,56,70,60,71,93,6,33,82,30,52,83,77,61,51,41,56,14,2};
        Test::areEqual( execute(arr4, 100,1), executeNaive(arr4,100, 1), "testSpecific3" );
        
        int arr5[] = { 64,61,98,1,61,47,55,28,65,80,87,5,1,65,21,37,98,98,22,46,87,35,72,78,44,42,68,49,52,8,47,69,21,46,70,82,93,25,62,58,5,2,63,59,67,36,48,18,34,70,16,73,57,88,4,1,83,24,2,35,32,50,4,5,96,26,39,41,52,1,99,57,55,14,16,75,50,65,93,36,87,61,62,97,50,66,50,85,90,53,20,74,3,77,79,99,3,18,92,7,71,91,65,27,57,33,54,59,50,99,47,90,60,9,87,62,27,37,47,69,90,20,95,93,49,74,44,4,44,36,12,16,79,29,95,88,62,49,47,65,0,95,7,60,56,94,75,36,83,22,5,74,94,1,19,43,27,16,0,72,4,12,40,36,41,87,24,55,88,24,72,88,71,79,0,79,25,75,15,9,50,73,35,96,26,54,40,53,22,92,77,79,4,69,15,97,56,91,4,96,67,77,36,90,8,37,70,34,64,85,43,14,58,78,11,36,84,3,90,59,95,19,38,51,89,5,0,97,96,4,46,16,33,82,6,94,71,76,28,36,14,23,2,24,53,13,61,37,68,51,48,15,70,86,18,11,91,18,61,40,75,7,56,60,89,62,54,61,39,34,49,5,57,51,29,62,17,90,0,85,41,0,53,64,87,71,27,78,90,88,70,17,95,26,77,37,41,84,50,32,18,99,37,28,50,66,90,67,57,42,5,50,43,58,66,30,29,94,60,71,34,31,40,82,9,18,19,50,54,21,82,72,72,19,0,22,86,43,42,95,85,99,45,28,9,12,10,38,58,71,62,92,54,2,74,15,72,45,18,26,66,0,51,38,72,3,13,10,46,7,5,32,6,2,60,67,14,23,5,72,46,19,17,0,74,91,67,98,37,85,77,55,38,28,94,10,31,59,72,78};
        Test::areEqual( execute(arr5, 400,2), executeNaive(arr5,400, 2), "testSpecific4" );
     
        int arr6[] = {74,56,91,21,39,14,72,84,32,92,72,66,93,54,55,17,82,50,67,99,96,80,94,77,25,84,30,4,34,47,48,8,56,91,29,47,6,54,83,90,98,8,56,91,14,63,9,96,14,76,96,62,8,90,39,86,26,69,90,60,69,90,69,25,34,50,24,40,4,8,30,54,16,38,46,30,2,55,78,68,83,74,30,43,16,70,29,94,39,72,55,60,14,76,37,48,78,62,40,35,70,22,89,38,61,35,20,63,42,98,31,77,25,61,21,41,83,2,36,75,26,43,35,93,71,73,41,49,35,82,36,57,56,26,47,17,13,67,32,8,65,15,85,90,29,58,84,12,61,72,39,39,15,75,32,38,0,74,39,87,8,76,96,16,54,43,34,19,10,18,27,27,34,65,70,63,23,54,27,36,26,67,76,93,94,8,31,46,34,70,33,94,98,29,11,4,24,97,24,86,15,51,13,1,16,83,64,92,89,44,28,15,11,56,60,57,65,91,3,51,14,36,98,64,17,61,69,41,10,93,27,77,96,92,79,65,28,95,9,17,39,37,85,2,46,45,59,11,89,62,14,55,50,64,19,19,77,40,12,87,85,91,65,34,84,44,51,12,39,60,81,79,49,66,81,95,64,93,58,53,7,25,60,10,89,31,29,67,72,42,6,57,85,71,43,69,15,94,33,7,6,15,38,56,33,71,51,97,64,62,2,24,87,62,34,76,94,63,95,66,57,2,75,43,73,71,64,41,65,50,0,72,17,38,80,50,9,31,0,26,93,2,50,32,65,84,61,11,99,56,29,9,10,56,4,36,27,68,29,93,18,29,17,87,67,97,38,28,80,38,54,26,40,4,58,57,40,19,68,40,28,97,1,38,6,5,26,33,73,55,78,44,84,95,31,3,44,21,32,77};
        Test::areEqual( execute(arr6, 400,2), executeNaive(arr6,400, 2), "testSpecific5" );
        
        int arr7[] = { 41,37,73,10,72,73,50,52,27,72,40,51,60,93,61,51,30,14,33,15,24,64,3,35,39,44,53,87,57,28,71,50,65,96,12,37,69,14,42,49,87,82,52,99,75,14,50,57,80,35,24,4,99,28,91,38,24,97,78,33,25,1,36,90,50,0,79,19,15,21,20,54,55,73,5,83,39,7,40,19,42,17,75,93,97,19,84,73,16,14,59,41,15,95,83,65,47,14,37,62,88,57,68,43,82,73,78,73,80,19,44,74,36,20,20,33,91,4,6,7,18,65,0,33,12,83,51,12,49,88,26,37,45,95,81,80,20,59,53,1,30,98,27,18,70,47,3,61,51,62,20,21,79,20,7,44,55,58,56,4,46,82,42,43,29,75,23,50,86,29,51,69,27,78,87,49,26,43,10,29,5,30,51,36,2,10,80,9,68,36,13,66,71,7,9,0,34,85,2,21,66,53,90,45,84,29,94,62,72,56,43,29,38,46,66,40,56,46,49,24,35,14,42,58,74,4,58,8,89,61,81,7,66,23,52,2,53,46,16,77,2,60,59,40,6,25,32,15,23,33,91,10,47,34,68,21,38,79,82,79,92,63,86,10,87,38,13,92,84,29,21,38,41,80,30,0,57,62,15,81,95,6,43,94,40,12,68,30,91,50,9,83,65,47,93,4,85,58,48,21,40,22,11,81,2,41,33,12,3,48,45,50,55,88,45,47,0,13,30,43,15,91,26,32,39,72,37,76,30,85,98,22,59,9,56,62,51,89,26,6,38,71,57,45,11,54,44,12,19,74,55,34,66,34,18,57,58,55,33,40,93,31,63,52,93,19,66,96,8,44,54,98,15,63,43,27,17,40,91,36,66,98,22,32,84,93,41,42,48,75,83,93,58,98,98,3,17,64,99,77};
        Test::areEqual( execute(arr7, 400,3), executeNaive(arr7,400, 3), "testSpecific6" );
    }
    
    static void testBenchmark(){ 
        int n = 900000;
        int * large = generateRandomArray(n,0,100);
        int k = 1;
        execute(large, n, k);
        
    }
private:
    static string arrayToString(int * array, int n){
        stringstream strStream;
        strStream << "{ ";
        for (int i = 0 ; i < n ; i++){
            if (i > 0){
                strStream << ",";
            }
            strStream << array[i];
        }
        strStream << "}";
        return strStream.str();
    }
    
    static int randomInt(int minVal, int maxVal ){
        return rand() % maxVal + minVal;
    }

    static int* generateRandomArray(int n, int minVal, int maxVal ){
        
        int * result = new int[n];
        for (int i = 0 ; i < n; i++){
            result[i] = randomInt(minVal, maxVal);
        }
        return result;
    }
};

////////////////////////////////////////////////////////////////////




int main(int argc, char **argv)
{  
    
    //Test::testExecute();
    //Test::testHash();
    //Test::testSpecific();
    //Test::testBatch(100,100000, 5);
    
    //Test::testBenchmark();
    //return 0;
    
    
    
    int n,k;
    cin >> n >> k;
    int * input = new int[n];
    for (int i = 0; i < n ; i++){
        int a; 
        cin >> a;
        input[i] = a;
    }
    
    pair<int, int> res = execute(input, n , k );
    
    if ( res.first == -1){
        cout << -1 << endl;
    }else{
        cout << res.first << " " << res.second << endl;
    }
    delete [] input;
    
    //testHash();
    //testExecute();
    
    return 0;
}