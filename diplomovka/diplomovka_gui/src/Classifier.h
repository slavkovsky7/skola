#ifndef _CLASSIFIER_H_
#define _CLASSIFIER_H_

#include <caffe/caffe.hpp>
#include <caffe/net.hpp>
#include <caffe/util/io.hpp>
#include <caffe/util/upgrade_proto.hpp>

#ifdef USE_OPENCV
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#endif  // USE_OPENCV
#include <algorithm>
#include <iosfwd>
#include <memory>
#include <string>
#include <utility>
#include <vector>
#include <memory>
#include <exception>


using namespace caffe;  // NOLINT(build/namespaces)
using namespace std;


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


static std::vector<unsigned char> readAllBytes(const std::string& filename) {
    if (filename.empty()) {
        return std::vector<unsigned char> ();
    }
    std::ifstream ifs;
    ifs.open(filename.c_str(), ios::binary|ios::ate);
    std::ifstream::pos_type pos = ifs.tellg();
    std::vector<unsigned char>  result(static_cast<unsigned int>(pos));
    ifs.seekg(0, ios::beg);
    ifs.read( reinterpret_cast<char*>(&result[0]), pos);
    ifs.close();
    return result;
}

class CaffeSolver {
private:
    caffe::Net<float>* net;
    caffe::Blob<float> meanBlob;
    caffe::Blob<float> inputBlob;    
    
    int inputWidth;
    int inputHeight;
    int inputChannels;
    
    int outputBlobsCount;
    int outputsCount;

    float inputScaleValue;
public:    
   CaffeSolver(const std::string& netStructurePath, const std::string& netModelPath, const std::string& meanPath, float inputScaleValue) {
        
        this->inputScaleValue = inputScaleValue;
        
        std::vector<unsigned char> netStructure = readAllBytes(netStructurePath);
        std::vector<unsigned char> netModel = readAllBytes(netModelPath);
        std::vector<unsigned char> mean = readAllBytes(meanPath);
        
        caffe::NetParameter param;

    
        this->net = new caffe::Net<float>(netStructurePath, caffe::TEST);
        this->net->CopyTrainedLayersFrom(netModelPath);
        
        
        this->outputBlobsCount =  net->output_blobs().size();
        this->outputsCount = 0;
        for (int i = 0 ; i < outputBlobsCount; i++) {
            this->outputsCount += net->output_blobs()[i]->count();
        }
        this->inputWidth    = net->input_blobs()[0]->width();
        this->inputHeight   = net->input_blobs()[0]->height();
        this->inputChannels = net->input_blobs()[0]->channels();
        this->inputBlob.Reshape(1, inputChannels, inputHeight, inputWidth);
    
        if (inputChannels != 1) {
            throw std::invalid_argument("Solver supports only one channel inputs");
        }
    
        if (mean.size() > 0 ) {
            caffe::BlobProto meanProto;
            caffe::ReadProtoFromMemoryOrDie(&mean[0], mean.size(), &meanProto);
            meanBlob.FromProto(meanProto, true);

            if (meanBlob.shape(1) != inputChannels || meanBlob.shape(2) != inputHeight || meanBlob.shape(3) != inputWidth) {
                throw std::out_of_range("Net input blob has a different shape than net mean blob");                
            }
        } else {
            meanBlob.Reshape(inputBlob.shape());
            float* meanData = meanBlob.mutable_cpu_data();
            std::memset(meanData, 0, sizeof(float) * meanBlob.count());
        } 
    }
    
    std::vector<float> solve(const cv::Mat& inputData) {
        std::vector<caffe::Blob<float>*> input, results;
        cv::Point pt;
        float *inputBlobData = inputBlob.mutable_cpu_data();
        const float *meanBlobData = meanBlob.cpu_data();
        for(pt.y = 0; pt.y < inputHeight; pt.y++) {
            for(pt.x = 0; pt.x < inputWidth; pt.x++) {
                int offset = inputBlob.offset(0, 0, pt.y, pt.x);
                *(inputBlobData + offset) = inputScaleValue * (inputData.at<unsigned char>(pt) - *(meanBlobData + offset));
            }
        }
        input.push_back(&inputBlob);
    
        results = net->Forward(input);

        std::vector<float> outputValues;
        for (int blobIdx = 0; blobIdx < outputBlobsCount; blobIdx++) {
            for (int resIdx = 0; resIdx < results[blobIdx]->count(); resIdx++ ) {
                outputValues.push_back(  results[blobIdx]->cpu_data()[resIdx] );
            }
        }
        return outputValues;
    }
};




#endif


























