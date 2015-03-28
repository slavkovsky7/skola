<?php

require './article.php';
    

class HtmlElement{
    static function GetA_ID( $id, $href, $name){
        return "<a" . ( $id != null  ? (" id=\"".$id."\""): "" ) . " href=\"".$href."\">".$name."</a>";   
    }
}

class GlobalControler{
    private $navigation;
    private $initalized;
    private $articles;
    
    public function GetNavigation(){ return $this->navigation; }
    
    public function AddArticle($title, $content, $date){
        $result = new Article($title, $content, $date);
        array_push($this->articles, $result);
        return $result;
    }
    
    public function GetArticle(){
        if(isset( $_GET["article"] ) ){
            $art = $_GET['article'];
            foreach ($this->articles as $article ) {
                if ( $article->pageName == $art ){
                    return $article;
                }
            }
        }
        return null;
    }
    
    public function ShowArtiles(){
        $articlesPerPage = 5;
        $countArts = count($this->articles);
        $page = 0;
        if(isset( $_GET["page"] ) ){
            $page = $_GET['page'];
        }
        $startIndex = $page*$articlesPerPage;
        for ($c=0, $i=$startIndex; $c < $articlesPerPage && $i<$countArts; $i++,$c++) {
           $this->articles[$i]->Show(true);
        }
        
        if ( $page > 0){
             echo HtmlElement::GetA_ID("prev", "index.php?page=".($page - 1 ),"Prev");
        }
        
        if ( $startIndex + $articlesPerPage < $countArts ){
            echo HtmlElement::GetA_ID("next", "index.php?page=".($page + 1 ),"Next");
        }
    }

    public function ShowArchive(){
        echo '<h3>Archive</h3>' ;
        echo '<ul id="archive">';
        $lastMonth = -1;
        $lastYear = -1;
        foreach ($this->articles as $art ) {
            $date = new DateTime();
            $date->setTimestamp( $art->dateAdded );
            $month = $date->format("m");
            $year = $date->format("Y");
            
            if ( $lastMonth != $month || $lastYear != $year ){
                if ( $lastMonth != -1){
                    echo '  </ul>';
                    echo '</li>';
                }
                echo '<li>'.$date->format('M Y');
                echo '  <ul>';
                $lastMonth = $month;
                $lastYear = $year;
            }
            echo '<li><a href="'.$art->GetLink().'">'.$art->title.'</a></li>';
        }
        if ( $lastMonth >= 0 ){
            echo '</ul></li>';
        }
        echo '</ul>';
        //http://stackoverflow.com/questions/3207749/i-have-2-dates-in-php-how-can-i-run-a-foreach-loop-to-go-through-all-of-those-d
    }
    
    public function InitComponents(){
        if ( !$this->initalized ){
            $this->navigation->AddLink("index.php", "Home" );
            //$this->navigation->AddLink("blog.php", "Blog");
            //$this->navigation->AddLink("contact.php", "Contact");
            $this->initalized = true;
            
            ////////////////////////////////////////////////////////////
        
            //Comment::LoadFromDatabase();
            //$this->articles = Article::LoadAllArticles();
            
            //echo "count =" . count(Comment::$allComments);
            $this->AddArticle("Title older 2", "test archivu fsdfsd" , "2.1.2013" )->saveToDatabase();
            $this->AddArticle("Title even older", "test archivu afasfaf"  , "3.5.2013" )->saveToDatabase();

            usort($this->articles, array('Article','cmp'));
        }
    }
    
    static function getInstance(){     
        global $instance;
        if (!is_object($instance)){           
            //if(!session_id()) session_start();    
            if ( !isset( $_SESSION['controler'] ) ){
                $instance = new GlobalControler();
                $instance->InitComponents();
                $_SESSION['controler'] = $instance;
                //echo "Creating controlel";
            }else{
                //echo "Using controlel from session";
                $instance = $_SESSION['controler'];
            }
        }
        return $instance;
    }
    
    function __construct() {
        $this->navigation = new Navigation();
        $this->initalized = false;
        $this->articles = array();
    }
    
    static function ShowHeader(){
        echo "<!DOCTYPE html>";
        echo "<html>";
        echo "  <head>";
        echo '      <meta charset="UTF-8">';
        echo "      <title>Martin Slavkovský</title>";
        echo '      <link rel="stylesheet" type="text/css" href="images/style.css">';
        echo "  </head>";
        echo "  <body>";
        echo '      <div id="page">';
        echo '          <div id="header">';
        echo '              <h1 id="logo-text">Martin Slavkovský</h1>';
        echo '          </div>';		
    }
    static function ShowFooter()
    {
        echo '      </div>'
        .    '      <div id="footer"> © Martin Slavkovsky 2015</div>'
        .    '  </body>'
        .    '</html>';
    }
    
    
    static $aboutMeText = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Donec libero. Suspendisse bibendum. Cras id urna. Morbi tincidunt, orci ac convallis aliquam, lectus turpis varius lorem, eu posuere nunc justo tempus leo. Donec mattis, purus nec placerat bibendum, dui pede condimentum odio, ac blandit ante orci ut diam.";
    
    static function ShowAboutMe(){
        echo ' <h3>About</h3>';
        echo '    <p> ';
        echo '            <a><img src="images/me.gif" width="240" height="180" alt="" class="float-left" /></a>';
        echo ' <br>'.self::$aboutMeText;
        echo ' </p>';
    }
    
    static function ShowSearchButton(){
        echo '    <h3>Search</h3>';
        echo '    <form id="qsearch" action="" method="get" >';
        echo '             <p>';
        echo '              <label for="qsearch"></label>';
        echo '              <input class="tbox" type="text" name="qsearch" value="Search this site..." title="Start typing and hit ENTER" />';
        echo '              <input class="btn" alt="Search" type="image" name="searchsubmit" title="Search" src="images/search.gif" />';
        echo '            </p>';
        echo '    </form>';
    }
    
    static function ShowSideBar(){
        echo '<div id="sidebar">';     
        GlobalControler::getInstance()->ShowAboutMe(); 
        GlobalControler::getInstance()->ShowSearchButton(); 
        GlobalControler::getInstance()->ShowArchive();
        echo '</div>';
    }
    
    private static function redirectTo($extra){
        $host  = $_SERVER['HTTP_HOST'];
        $uri   = rtrim(dirname($_SERVER['PHP_SELF']), '/\\');
        //echo "redirecting";
        header("Location: http://$host$uri/$extra");
    }
    
    static function ShowCommentForm($commentID, $article ){
       //echo "commentID = $commentID";
       session_start();
       if ( isset( $_POST['name'] ) && !isset( $_SESSION['dup_name'] ) && $_POST['name'] != "" && $_POST['name'] != "Your Name" ){         
           $_SESSION['dup_name'] = $_POST['name'];
           if ($article != null  ){
                $newComment = $article->AddComment($_POST['name'], $_POST['message'], time(), $commentID );
                //TODO::TUTO ZAPISAT DO DB;
                $newComment->SetArticle($article);
                if ( !isset($_GET['react']) ){
                    $commentID = $article->rootComment->GetID();
                }else{
                    $commentID = $_GET['react'];
                }
                $newComment->saveToDatabase($commentID);
                
                $extra = "blog.php?article=".$article->pageName."&#comment-".$newComment->GetID();
                self::redirectTo($extra);
           }
       }else{
           if (isset($_SESSION['dup_name'])){
               unset($_SESSION['dup_name']);
           }         
           self::showForm(10,20);
       }
    }
    
    private static function showForm($rows, $cols){
      echo '<div id="comments-form">'
           .'    <h3 id="respond">Leave a Reply</h3>'
           .'    <form action="" method="post" id="commentform">'
           .'        <p>'     
           .'            <label for="name">Name (required)</label>'
           .'            <br />'
           .'            <input id="name" name="name" value="Your Name" type="text" tabindex="1" />'
           .'        </p>'
           .'        <p>'
           .'            <label for="message">Your Message</label>'
           .'            <br />'
           .'            <textarea id="message" name="message" rows="'.$rows.'" cols="'.$cols.'" tabindex="4"></textarea>'
           .'        </p>'
           .'        <p class="no-border">'
           .'            <input class="button" type="submit" value="Submit" tabindex="5" />'
           .'        </p>'
           .'    </form>'
           .'</div>';
    }
    
    public static function ShowPostForm(){
       session_start();
       if ( isset( $_POST['name'] ) && $_POST['name'] != "" && $_POST['message'] != "" ){         
            $article = new Article( $_POST['name'], $_POST['message'], time(), null );
            $article->saveToDatabase();
            echo "<h3>Article saved to db</h3>";
            header('Location: '. "index.php");
       }else{
            self::showForm(30,50);
       }
    }
    
}

class Navigation{
    private $linkMap;
    
    function __construct() {
        $this->linkMap = array();
    }
    
    public function AddLink( $href, $name ){
        $this->linkMap[$href] = $name;
    }

    public function __toString()
    {
        $result = "<div id=\"navigation\">";
        foreach ($this->linkMap as $href => $value ){
            $id = null;
            if ( $_SERVER['PHP_SELF'] === ("/".$href) ){
                $id = "current";
            }
            $result = $result . HtmlElement::GetA_ID($id, $href, $value);
        }
        $result = $result . "</div>";
        return $result;
    }
    
    public function Show(){
        echo (string)$this;
    }
}