1.Data

 - http://uloz.to/x5Rpyeye/imdb-data-zip
 - aj ked su data volne dostupne na imdb stranke trochu som tie subory upravoval aby sa lahsie parsovali. Zmazal som z nich licenciu atd.
 - rozbalene data by mali byt v tychto cestach
    ./data/imdb_data/genres.list
    ./data/imdb_data/plot.list

2.Ako spustit
 - program bude bezat len po linuxom
 - treba si pozriet subor mc.properties kde sa daju nastavovat niektore parametre programu. Popis je vnutri.
 - buildovat to netreba uz vsetko je bin, ale pre pripad som pripravil skript , ale keby nahodou tak prikazom 
    
    javac -d bin/ src/*
 - potom pustit run.sh , mozne parametre su

 Pre binary approach : 
 -gen-bdist  -> vygeneruje $GENRE_plain.txt subory
 -fil-bdist  -> vyfiltruje $GENRE_plain.txt a ulozi ako $GENRE_stemmed.txt
 -svmb       -> vyrobi z $GENRE_stemmed.txt
 -trainb     -> zavola svm-train a vyrobi $GENRE_train.svm.model a $GENRE_predicted.o, v skutocnosti to trenuje aj robi predikciu
 -mb	     -> vypise statistiky
 
 - prikazy mozeme pustit aj naraz. But cen 
      ./run_complete_binary.sh 
  alebo
      ./run.sh -gen-bdist -fil-bdist -svmb -trainb -mb
 
 - mali by byt spustane v tomto poradi
 
 
3.Parametre pre label approach
 
 -gen-tdist  -> vygeneruje plain.txt subory
 -fil-tdist  -> vyfiltruje plain.txt a ulozi ako stemmed.txt
 -svmt       -> vyrobi z $GENRE_stemmed.txt
 -traint     -> zavola svm-train a vyrobi train.svm.model a predicted.o, v skutocnosti to trenuje aj robi predikciu
 -mt	     -> vypise statistiky
 
 ./run_complete_label.sh
alebo
 ./run.sh -gen-tdist -fil-tdist -svmt -traint -mt