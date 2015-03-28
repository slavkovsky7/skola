<?php 
    require './common.php';

    GlobalControler::getInstance()->ShowHeader();
    GlobalControler::getInstance()->GetNavigation()->Show();
?>

<div id="section">		
    <div id="content">
<?php 
    $valid = false;
    if ( isset($_GET['user']) && isset($_GET['pass'] ) ) {
        $user = $_GET['user'];
        $pass = $_GET['pass'];
        if ($user == "martin" && $pass == "martin666"){
            $valid = true;
            GlobalControler::getInstance()->ShowPostForm();
        }
    }
    
    if (!$valid){
            echo "<h3>Articles can be only added by page owner, so knock it off !! </h3>";
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