#include "q_normalizer.h"
#include <math.h>
#include <malloc.h>
#include <string.h>
#include <stdexcept>

QNormalizer::QNormalizer()
{
    isInitialized = false;
    posQuant = nullptr;
    negQuant = nullptr;
    posSize = 0;
    negSize = 0;
    power = 0;
}

QNormalizer::QNormalizer(const QNormalizer &other)
{
    isInitialized = other.isInitialized;
    posHigherNeg = other.posHigherNeg;
    type = other.type;
    posSize = other.posSize;
    negSize = other.negSize;
    posQuant = (int*)malloc(posSize*sizeof(int));
    memcpy(posQuant, other.posQuant, posSize*sizeof(int));
    negQuant = (int*)malloc(negSize*sizeof(int));
    memcpy(negQuant, other.negQuant, negSize*sizeof(int));
    power = other.power;
}

QNormalizer::~QNormalizer()
{
    FreeAll();
}

void QNormalizer::InitDetector(const int *qPos, const int qPosSize, const int *qNeg, const int qNegSize, const int pwr, const bool posHigher)
{
    if(pwr==0)
        throw std::invalid_argument("pwr equals 0");
    InitClassifier(qPos, qPosSize, qNeg, qNegSize, posHigher);
    power = pwr;
    type = DETECTOR;
}

void QNormalizer::InitClassifier(const int *qPos, const int qPosSize, const int *qNeg, const int qNegSize, const bool posHigher)
{
    if(qPos==nullptr || qPosSize==0 || qNeg==nullptr || qNegSize==0)
        throw std::invalid_argument("InitClassifier failed, invalid arguments");
    FreeAll();
    power = 0;
    posHigherNeg = posHigher;
    posSize = qPosSize;
    posQuant = (int*)malloc(posSize*sizeof(int));
    memcpy(posQuant,qPos,posSize*sizeof(int));
    negSize = qNegSize;
    negQuant = (int*)malloc(negSize*sizeof(int));
    memcpy(negQuant,qNeg,negSize*sizeof(int));
    isInitialized = true;
    type = CLASSIFIER;
}

void QNormalizer::InitPreValue(const int *qPos, const int qPosSize, const int *qNeg, const int qNegSize, const bool posHigher)
{
    InitClassifier(qPos, qPosSize, qNeg, qNegSize, posHigher);
    type = PREVALUE;
}

void QNormalizer::FreeAll()
{
    isInitialized = false;
    if(posQuant!=nullptr)
    {
        free(posQuant);
        posQuant = nullptr;
    }
    if(negQuant!=nullptr)
    {
        free(negQuant);
        negQuant = nullptr;
    }
}

int QNormalizer::Normalize(const int value)
{
    if(!isInitialized)
        return value;

    int posIndex = Bisect(posQuant, posSize, value);
    int negIndex = Bisect(negQuant, negSize, value);
    int normalized;
    switch(type)	
    {
        case DETECTOR:	// detector 0..10000
            normalized = int(10000. * pow(.5*posIndex/posSize + .5*negIndex/negSize, power));
            if(posHigherNeg)
                return normalized;
            else
                return 10000-normalized;
            break;
        case CLASSIFIER:	// classifier -10000..+10000
            normalized = 10000*posIndex/posSize + 10000*negIndex/negSize - 10000;
            if(posHigherNeg)
                return normalized;
            else
                return -normalized;
            break;
        case PREVALUE:	// preValue 0..+10000
            normalized = 5000*posIndex/posSize + 5000*negIndex/negSize;
            if(posHigherNeg)
                return normalized;
            else
                return 10000-normalized;
            break;
        default:
            return value;
    }
    
}

int QNormalizer::Bisect(const int *quantiles, const int size, const int val)
{ 
    int min_idx = 0;
    int max_idx = size-1;

    if(val <= quantiles[min_idx])
        return min_idx;
    if(val >= quantiles[max_idx])
        return max_idx+1;

    while(true)
    {
        if((max_idx-min_idx)==1)
            return max_idx;
        int bisect_idx = (min_idx+max_idx)/2;
        if(val == quantiles[max_idx])
            return (max_idx+1);
        if(val < quantiles[bisect_idx])
            max_idx = bisect_idx;
        else
            min_idx = bisect_idx;
    }
}
