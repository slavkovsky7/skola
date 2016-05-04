#ifndef _QNORMALIZER_H_
#define _QNORMALIZER_H_

class QNormalizer
{
    typedef enum {DETECTOR,CLASSIFIER,PREVALUE} NormType;
public:
    QNormalizer();
    QNormalizer(const QNormalizer &other);
    ~QNormalizer();
    void InitDetector(const int *qPos, const int qPosSize, const int *qNeg, const int qNegSize, const int pwr, const bool posHigher);
    void InitClassifier(const int *qPos, const int qPosSize, const int *qNeg, const int qNegSize, const bool posHigher);
    void InitPreValue(const int *qPos, const int qPosSize, const int *qNeg, const int qNegSize, const bool posHigher);
    int Normalize(const int value);
    void FreeAll();
private:
    bool isInitialized;
    bool posHigherNeg;
    NormType type;
    int *posQuant;
    int *negQuant;
    int posSize;
    int negSize;
    int power;
    int Bisect(const int *quantiles, const int size, const int val);
};

#endif