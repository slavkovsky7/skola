<?php 
    require './common.php';

    GlobalControler::getInstance()->ShowHeader();
    GlobalControler::getInstance()->GetNavigation()->Show();
?>
<div id="section">		
    <div id="content">
    <?php
        GlobalControler::getInstance()->ShowArtiles();
    ?>
    </div>
    <?php     
        GlobalControler::getInstance()->ShowSideBar();      
    ?>
</div>		
<?php 
    GlobalControler::getInstance()->ShowFooter();
?>