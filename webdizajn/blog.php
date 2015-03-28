<?php 
    require './common.php';

    GlobalControler::getInstance()->ShowHeader();
    GlobalControler::getInstance()->GetNavigation()->Show();
?>

<div id="section">		
    <div id="content">
<?php
    $art = GlobalControler::getInstance()->GetArticle();
    if ($art != null){
        $art->Show(false);
    }else{
        echo "No article";
    }
    
    if ( !isset($_GET['react']) ){
        GlobalControler::getInstance()->ShowCommentForm(0, $art );
    }
?>
    </div>
    <?php     
        GlobalControler::getInstance()->ShowSideBar();      
    ?>
</div>		
<?php 
    GlobalControler::getInstance()->ShowFooter();
?>