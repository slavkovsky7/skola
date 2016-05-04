#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <stdio.h>
#include <utility>

#include "constants.h"
#include "findEyeCenter.h"

using namespace std;
using namespace cv;

/*************************************************/
#include "Classifier.h"

/*************************************************/
vector<double> findTransformation(Point src1, Point src2, Point trg1, Point trg2) {
    if (src2.x < src1.x) {
        std::swap(src1, src2);
    }
    Point srcVec = src2 - src1;
    Point srcVecNorm ( -srcVec.y, srcVec.x );
    Point src3 = src1 - srcVecNorm;
    
    Point trgVec = trg2 - trg1;
    Point trgVecNorm( -trgVec.y, trgVec.x );
    Point trg3 = trg1 - trgVecNorm;
    
    double x1 = src1.x;
    double y1 = src1.y;
    double x2 = src2.x;
    double y2 = src2.y;
    double x3 = src3.x;
    double y3 = src3.y;
    
    double srcData[] = { 
      x1,y1,1 , 0, 0, 0, 0, 0, 0,
      x2,y2,1 , 0, 0, 0, 0, 0, 0,
      x3,y3,1 , 0, 0, 0, 0, 0, 0,
       0, 0,0 ,x1,y1, 1, 0, 0, 0,
       0, 0,0 ,x2,y2, 1, 0, 0, 0,
       0, 0,0 ,x3,y3, 1, 0, 0, 0,
       0, 0,0 , 0, 0, 0,x1,y1, 1,
       0, 0,0 , 0, 0, 0,x2,y2, 1,
       0, 0,0 , 0, 0, 0,x3,y3, 1 };
    Mat src(9,9,CV_64F,srcData);
    x1 = trg1.x;
    y1 = trg1.y;
    x2 = trg2.x;
    y2 = trg2.y;
    x3 = trg3.x;
    y3 = trg3.y; 
    
    double destData[] = {x1,x2,x3,y1,y2,y3,1,1,1};
    Mat dest(9,1, CV_64F, destData);
    Mat trVec = src.inv() * dest; 
    
    //TODO::
    double resData[] = { 
        trVec.at<double>(0,0), trVec.at<double>(1,0), trVec.at<double>(2,0),
        trVec.at<double>(3,0), trVec.at<double>(4,0), trVec.at<double>(5,0)
        /*trVec.at<double>(6,0), trVec.at<double>(7,0), trVec.at<double>(8,0)*/ };
        
    return  vector<double>(resData, resData + 6 );
}

Mat toGray(const Mat& img) {
    Mat result;
    cvtColor(img, result, CV_BGR2GRAY);
    return result;
}

cv::Mat MakeAffineTrasformation(const cv::Point& srcRightEye,const  cv::Point& destRightEye, const cv::Point& srcLeftEye, const cv::Point& destLeftEye) {
    vector<double> coefs = findTransformation(srcRightEye, srcLeftEye, destRightEye, destLeftEye);
    cv::Mat result(2,3, CV_64F, &coefs[0]);
    return result.clone();
}

cv::Mat normalize(
      const cv::Mat& img,
      const cv::Point& rightEye,
      const cv::Point& leftEye, 
      const cv::Size& trainingImageSize,
      const cv::Point2f& trainingImageREye,
      const cv::Point2f& trainingImageLEye,
      const cv::Size& finalSize
      ) {
    //const cv::Size trainingImageSize(140, 170);
    //const cv::Point2f trainingImageREye(50, 70);
    //const cv::Point2f trainingImageLEye(100, 70);

    cv::Mat affineTrans = MakeAffineTrasformation(rightEye, trainingImageREye, leftEye, trainingImageLEye);
    cv::Mat solverImg;
    cv::imwrite("original.png",img);
    cv::warpAffine(img, solverImg, affineTrans, trainingImageSize, cv::INTER_LINEAR, cv::BORDER_REPLICATE);
    cv::imwrite("affine.png",solverImg);
    solverImg = toGray(solverImg);
    cv::imwrite("gray.png",solverImg);
    cv::resize(solverImg, solverImg, finalSize);
    cv::imwrite("final.png",solverImg);
    return solverImg;
}


vector<cv::Point> findEyes(cv::Mat frame, cv::Rect face) {
  cv::Mat faceROI = frame(face);
  cv::Mat debugFace = faceROI;

  if (kSmoothFaceImage) {
    double sigma = kSmoothFaceFactor * face.width;
    GaussianBlur( faceROI, faceROI, cv::Size( 0, 0 ), sigma);
  }
  //-- Find eye regions and draw them
  int eye_region_width = face.width * (kEyePercentWidth/100.0);
  int eye_region_height = face.width * (kEyePercentHeight/100.0);
  int eye_region_top = face.height * (kEyePercentTop/100.0);
  cv::Rect leftEyeRegion(face.width*(kEyePercentSide/100.0),
                         eye_region_top,eye_region_width,eye_region_height);
  cv::Rect rightEyeRegion(face.width - eye_region_width - face.width*(kEyePercentSide/100.0),
                          eye_region_top,eye_region_width,eye_region_height);

  //-- Find Eye Centers
  cv::Point leftPupil = findEyeCenter(faceROI,leftEyeRegion,"Left Eye");
  cv::Point rightPupil = findEyeCenter(faceROI,rightEyeRegion,"Right Eye");
  
  return std::vector<cv::Point> { rightEyeRegion.tl()+rightPupil, leftEyeRegion.tl() + leftPupil };
}

void detectAndDisplay(CascadeClassifier& face_cascade, CaffeSolver& genderSolver, CaffeSolver& ageSolver, cv::Mat frame ) {
  std::vector<cv::Rect> faces;
  cv::Mat frame_gray = toGray(frame);

  face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE|CV_HAAR_FIND_BIGGEST_OBJECT, cv::Size(200, 200) );

  for (int i = 0; i < faces.size(); i++) {
    auto& face = faces[i];
    auto eyes = findEyes(frame_gray, face );
    if (eyes.size() == 2) {
      auto normalizedAge    = normalize(frame, face.tl()+eyes[0], face.tl()+eyes[1], cv::Size(140,170), cv::Point2f(50,70) , cv::Point2f(100,70) , cv::Size(70,85));
      auto normalizedGender = normalize(frame, face.tl()+eyes[0], face.tl()+eyes[1], cv::Size(128,128), cv::Point2f(44,44) , cv::Point2f(84,44)  , cv::Size(64,64));
      
      auto age = ageSolver.solve(normalizedAge);
      auto gender = genderSolver.solve(normalizedAge);
      
      cv::circle(frame, face.tl()+eyes[0], 5, Scalar(0, 255, 0), 2, 2, 0);
      cv::circle(frame, face.tl()+eyes[1], 5, Scalar(0, 255, 0), 2, 2, 0);
    
      cv::rectangle(frame, face, 1234, 2);
    
      cv::imshow(std::string("face_age_")+std::to_string(i), normalizedAge);
      cv::imshow(std::string("face_gender_")+std::to_string(i), normalizedGender);
      
      stringstream ss;
      ss << "Age : " << age[0]*60.0;
      cv::putText(frame, ss.str(), cv::Point(50,50), cv::FONT_HERSHEY_PLAIN,2,Scalar(0,0,255), 2, 3);
      ss.str("");ss << "Male.conf   : " << gender[0];
      cv::putText(frame, ss.str(), cv::Point(50,80), cv::FONT_HERSHEY_PLAIN,2,Scalar(0,0,255), 2, 3);
      ss.str("");ss << "Female.conf : " << gender[1];
      cv::putText(frame, ss.str(), cv::Point(50,110), cv::FONT_HERSHEY_PLAIN,2,Scalar(0,0,255), 2, 3);
    }
  }
  cv::imshow("Main Capture", frame);
}


int main(int argc, const char** argv)
{
    CaffeSolver  gender_solver("gender/predict.prototxt", "gender/model.caffemodel", "gender/images_mean.binaryproto", 0.00390625f );
    CaffeSolver  age_solver("age/predict.prototxt", "age/model.caffemodel", "age/images_mean.binaryproto", 1);

    string face_cascade_name = "haarcascade_frontalface_alt.xml";

    CascadeClassifier face_cascade;


    if(!face_cascade.load(face_cascade_name)) {
        printf("--(!)Error loading\n");
        return -1;
    };

    VideoCapture capture( -1 ); 

    Mat frame;
    bool paused = false;
    while( true ) {
        if (!paused) {
           capture >> frame;
        }
        
        //frame = imread("sibyla.jpg");
        
        if(!frame.empty()) {
          detectAndDisplay(face_cascade, gender_solver, age_solver, frame);
        } else {
          cout << " --(!) No captured frame -- Break!" << endl;
          break;
        }

        char c = (char)waitKey(10);
        if(c == 'c') {
            break;
        } else if ( c == 'p' ) {
            paused = !paused;
        }
    }
    return 0;
}
