<?php
require './utils.php';

class Comment{
    private $userName;
    private $content;
    private $dateAdded;
    private $commentsIDs;
    private $id;
    private $article;
    
    public static $allComments;

    
    public function SetArticle( $article ){
        if ($article != null){
            $this->article = $article;
            $comments = $this->getMyComments();
            foreach ($comments as $comment) {          
                $comment->SetArticle($article);
            }
        }
    }
    
    public function __construct( $userName, $content, $date, $article ){
               
        $this->commentsIDs = array();
        if (!ctype_alnum ( $date )  ){
            $date = strtotime($date);
        }
        $this->userName = $userName;
        $this->content = $content;
        $this->dateAdded = $date;

        //TODO::Zmenit pri implementovani databazy
        if (is_null(self::$allComments)) { 
            self::$allComments = array();
        }
        
        //Preco je tu toto?
        //$this->id = count(self::$allComments);
        //self::$allComments[$this->id] = $this;
        
        $this->SetArticle($article);
    }
    
    public function GetID(){
        return $this->id;
    }
    
    public function SetID($id){
        $this->id =$id;
    }
    
    public function AddSubComment($commentID){
         array_push($this->commentsIDs, $commentID);
    }
    
    public function GetSubCommentsCount(){
        $result  = count($this->commentsIDs);
        foreach ($this->commentsIDs as $key => $value){
            //if ( array_key_exists($value, self::$allComments ) ){
                $com = self::$allComments[$value];
                if ( $com != null ){
                    $result = $result + $com->GetSubCommentsCount();
                }
            //}
        }
        return $result;
    }

    
    private function getMyComments(){
        $comments = array();
        foreach ( $this->commentsIDs as $key => $commentID ) {
            array_push($comments, self::$allComments[$commentID]);
        }
        return $comments;
    }
    
    public function Show($alt, $root ){
        $comments = $this->getMyComments();
        
        usort($comments, array('Comment','cmp'));
        if ( !$root ){
            
            $comment_react_href = $_SERVER['PHP_SELF'].'?article='.$_GET['article'].'&amp;react='.$this->GetID().'#comment-'.$this->GetID();
            
            $idAttr = 'id="comment-'.$this->GetID().'"';
            echo $alt ? '<li '.$idAttr.' class="alt">' : '<li '.$idAttr.' >';
            echo  '  <cite> <img alt="" src="images/gravatar.jpg" class="avatar" height="40" width="40" /> '.$this->userName.' Says: <br />'
                 .'  <span class="comment-data"><a>'.date('M d., Y',$this->dateAdded).' at '.  date('H:i',$this->dateAdded).'</a> </span> </cite>'
                 .'  <div class="comment-text">'
                 .'    <p>'.$this->content.'</p>'
                 .'  </div>'
                 .'  <a href="'.$comment_react_href.' ">React</a>';
        }
        
        if ( count($comments) > 0 ){
            echo '<ol class="commentlist">';
            $altSub = !$alt;
            foreach ($comments as $comment) {          
                $comment->Show($altSub,false);
                $altSub = !$altSub;
            }
            echo '</ol>';
        }
        
        if ( !$root ){
            if ( isset($_GET['react']) ){
                if ( $_GET['react'] == $this->GetID() ){ 
                    GlobalControler::getInstance()->ShowCommentForm($this->GetID(), $this->article );
                }
            }

            echo '</li>';
        }
    }
    
    public static function cmp($a, $b) 
    {
        return $a->dateAdded > $b->dateAdded;
    }
    
    public static function LoadFromDatabase(){
        $pdo = new PDO("sqlite:blog.db");
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); 
        $sql = "SELECT * FROM comments";
        foreach ($pdo->query($sql) as $row) {
             $comment = new Comment($row['username'], $row['content'], $row['date'], null);
             $comment->SetID($row['id']);
             $str = explode(",", $row['comment_ids'] );
             $comment->commentsIDs = array();
             //TMP::
             //echo "loading".$row['id']."<br>";
             $comment->SetID($row['id']);
             foreach ($str as $key => $value) {
                 if ($value != ""){
                    $comment->AddSubComment($value);
                 }
             }
             self::$allComments[$comment->GetID()] = $comment;
             /*
             echo "count = " . count(self::$allComments)."<br>";
             foreach (self::$allComments as $key => $value) {
                 echo "array " .$key. " => " . $value->GetID() ."<br>";
             }
             echo "-----------------------------<br>";
              * */
        }                   
    }
    
    public function saveToDatabase( $parentCommentID ){
        try {
                       
            $pdo = new PDO("sqlite:blog.db");
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            
            $dbCreate = "CREATE TABLE IF NOT EXISTS comments("
                    .'id INTEGER PRIMARY KEY AUTOINCREMENT     NOT NULL,'
                    .'comment_ids       TEXT               NOT NULL,'
                    .'username          VARCHAR(255)       NOT NULL,'
                    .'content           TEXT               NOT NULL,'               
                    .'article_page_name VARCHAR(255)       NOT NULL,'                             
                    .'date              INT                NOT NULL'
                    . ")";
            $pdo->exec( $dbCreate );
           
 
            $sql = "INSERT INTO comments (comment_ids,username,content,article_page_name,date) VALUES( "
                    .":comment_ids,"
                    .":username,"
                    .":content,"
                    .":article_page_name,"
                    .":date)";
            $st = $pdo->prepare($sql);
            $st->execute(  array(
                ":comment_ids"  =>  "",               
                ":username"     =>  $this->userName,
                ":content"      =>  $this->content,
                ":article_page_name"   =>  $this->article->pageName,
                ":date"         =>  time()
             ) );
            
            $sql = 'SELECT * FROM comments WHERE id=(SELECT MAX(id) FROM comments)';
            foreach ($pdo->query($sql) as $row) {
                 $this->id = $row['id'];
            }
            
            /*Ak sme teda ziskali uz ID z db mozeme updanut rodicovi jeho deti*/
            //TMP::         
            if ($parentCommentID > 0){
                $parent = self::$allComments[$parentCommentID];
                if ($parent != null){
                    $commentIDsString = "";
                    //Ako ja mozem pridat id pokial ho este nemam v db
                    $parent->AddSubComment( $this->GetID() );
                    //echo count($parent->commentsIDs) . "<br>";
                    foreach ( $parent->commentsIDs as $key => $commentID ) {
                        //echo "parent.subcommentID = " . $commentID . "<br>";
                        if ( $commentIDsString != "" ){
                            $commentIDsString = $commentIDsString . ",";
                        }
                        $commentIDsString  = $commentIDsString . $commentID;
                    }

                    //echo "Updating parent comment_ids  $parentCommentID -> $commentIDsString<br>" ;
                    $sql = "UPDATE comments 
                            SET comment_ids=?
                            WHERE id=?";

                    $st = $pdo->prepare($sql);
                    $st->execute(array($commentIDsString,$parentCommentID));
                }
            }

            
        }catch(PDOException $e){
            echo $e->getMessage();
        }
    }
}

class Article{
    public $title;
    public $pageName;
    public $dateAdded;
    public $content;
    
    public $rootComment;
    public $rootCommentID;
    
    
    public function __construct($title, $content, $date, $rootComment = false ) {
        $this->title = $title;
        $this->pageName = Utils::title2pagename($title);
        $this->content = $content;
        if ( !ctype_alnum ( $date ) ){
            $date = strtotime($date);
        }
        $this->dateAdded = $date;
        
        if ( $rootComment == null ){
            $this->rootComment = new Comment("","", time(), $this );
        }else{
            $this->rootComment = $rootComment;
        }
        /*$this->AddComment("Matko", "skurveny komentar", time() , null );
        $this->AddComment("Matko", "skurveny komentar", time() , null );
        $this->AddComment("Matko", "skurveny komentar", time() , null );
        $this->AddComment("Matko", "skurveny komentar", time() , null );
        $this->AddComment("Matko2", "skurveny komentar", time() - 1000 , null );
        $comment = $this->AddComment("Matko3", "skurveny komentar", time() - 2000 , null );
        $this->AddComment("Matko3", "skurveny komentar", time() - 1500 , $comment->GetID() );*/
    }
    
    public static function SearchArticles($str){
        $result = array();
        $pdo = new PDO("sqlite:blog.db");
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        //http://www.phparch.com/2011/11/full-text-search-with-sqlite/
        $sql = "SELECT * FROM articles_search WHERE title MATCH '$str'";
        foreach ($pdo->query($sql) as $row) {
            array_push( $result, self::LoadFromDatabase( $row['page_name'] ) );
        }
        return $result;
    }
    
    public static function LoadAllArticles(){
        $result = array();
        $pdo = new PDO("sqlite:blog.db");
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); 
        $sql = "SELECT page_name FROM articles";
        foreach ($pdo->query($sql) as $row) {
            array_push( $result, self::LoadFromDatabase( $row['page_name'] ) );
        }
        return $result;
    }
    
    public static function LoadFromDatabase($page_name){
        $result = null;
        $pdo = new PDO("sqlite:blog.db");
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); 
        $sql = "SELECT * FROM articles WHERE page_name='$page_name'";
        foreach ($pdo->query($sql) as $row) {
            //To znamena ze toto sa musi volat neskor ako loadovanie komentarov
            $rootComment = Comment::$allComments[$row['root_comment_id']];        
            $result = new Article($row['title'], $row['content'], $row['date'], $rootComment );
            $result->pageName = $page_name;
        }   
        return $result;
    }
    
    public function AddComment($userName, $content, $date, $prevCommentID ){
        $comment = new Comment($userName, $content, $date, $this);
        if ($prevCommentID != null){
            $prevComment = Comment::$allComments[$prevCommentID];
            $prevComment->AddSubComment($comment->GetID());
        }else{
            $this->rootComment->AddSubComment($comment->GetID()); 
        }
        return $comment;
    }
    
    public function GetLink(){
        return "blog.php?article=".$this->pageName;
    }
    
    public function Show( $preview ){
        $link = $this->GetLink();
        $content = substr( strip_tags($this->content) , 0, 200 );
        echo '<div class="' . ( $preview ? 'post' : 'post-full') .'">'
                .'<h2><a href="'.$link.'">'.$this->title.'</a></h2>'
                . ( $preview ? $content: $this->content);
        echo    '<p class="post-footer">';
        if ($preview ){
            echo     '<a href="'.$link.'" class="readmore">Read more</a> | '
                    .'<a href="'.$link.'" class="comments">Comments ('.$this->rootComment->GetSubCommentsCount().')</a> | ';
        }
        echo        '<span class="date">'. date('M d. Y',$this->dateAdded).'</span>'
                .'</p>'					
            .'</div>';
        if ( !$preview ){
            $this->showComments();
        }
    }
    
    private function showComments(){ 
        //TMP::
        //echo "rootID = " . $this->rootComment->GetID();
        $count = $this->rootComment->GetSubCommentsCount();
        $this->rootComment->SetArticle($this);
        if ( $count > 0 ){
            echo '<div id="comments">';
            echo '<h3>('.$count.') Responses</h3>';
            $this->rootComment->Show(false, true);
            echo '</div>';
        }
    }  
            
    public static function cmp($a, $b) 
    {
        return $a->dateAdded < $b->dateAdded;
    }
    
        
    public function saveToVirtualDatabase(){               
            $pdo = new PDO("sqlite:blog.db");
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
                       
            $sql= "SELECT name FROM sqlite_master WHERE type='table' AND name='articles_search' ";
            $exists = false;
            foreach ($pdo->query($sql) as $row) {
                $exists = true;
            }
            if (!$exists){
                $dbCreate = "CREATE VIRTUAL TABLE articles_search USING fts3(page_name TEXT PRIMARY KEY, title TEXT, content TEXT);";
                $pdo->exec( $dbCreate );
            }
            
            $sql = "DELETE FROM articles_search WHERE page_name='$this->pageName'";
            $pdo->exec($sql);
            
            $sql = "INSERT OR REPLACE INTO articles_search (page_name, title, content) VALUES( "
                .":page_name,"
                .":title,"
                .":content)";
            $st = $pdo->prepare($sql);
            $st->execute(  array(
                ":page_name"        =>  $this->pageName,
                ":title"            => Utils::toLower2($this->title),
                ":content"          => Utils::toLower2($this->content)
             ) );
    }
    
    public function saveToDatabase(){

        try {
            $pdo = new PDO("sqlite:blog.db");
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            
            $dbCreate = "CREATE TABLE IF NOT EXISTS articles("
                    .'page_name         VARCHAR(255) PRIMARY KEY       NOT NULL,'
                    .'title             VARCHAR(255)       NOT NULL,' 
                    .'content           TEXT               NOT NULL,'                                     
                    .'date              INT                NOT NULL,'
                    .'root_comment_id   INT                NOT NULL'
                    . ")";
            $pdo->exec( $dbCreate );
            
            //toto by malo nastavit root commentID
            $root_comment_id = -1;
            $sql = "SELECT * FROM articles WHERE page_name='$this->pageName'";
            foreach ($pdo->query($sql) as $row) {
                $root_comment_id = $row['root_comment_id'];
            }
            
            if ($root_comment_id == -1){
                //TMP::
                //echo "Saving root to db<br>";
                $this->rootComment->saveToDatabase($root_comment_id);//NULL
            }else{
                //TMP::
                //echo "Setting root to id ->".$root_comment_id ."<br>";
                $this->rootComment->SetID( $root_comment_id );
            }
            
            $sql = "INSERT OR REPLACE INTO articles (page_name,title,content,date,root_comment_id) VALUES( "
                    .":page_name,"
                    .":title,"
                    .":content,"
                    .":date,"
                    .":root_comment_id)";
            $st = $pdo->prepare($sql);
            $st->execute(  array(
                ":page_name"        =>  $this->pageName,               
                ":title"            =>  $this->title,
                ":content"          =>  $this->content,
                ":date"             =>  $this->dateAdded,
                ":root_comment_id"  =>  $this->rootComment->GetID()
             ) );
            
            $this->saveToVirtualDatabase();
            
        }catch(PDOException $e){
            echo $e->getMessage();
        }
    }
}