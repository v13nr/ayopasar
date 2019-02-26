<?php

    class crypto {

        public function crypto() {}
        
        public function encrypt($text) {
        
            $result = "";

            $value = Array(); //new String[10][10];
            for($b=0; $b<10; $b++) 
                for($k=0; $k<10; $k++)
                    $value[$b][$k] = ($b+$k>9?$b+$k-10:$b+$k);

            $key = "100476002";
            $intkey = 0;

            for($l=0; $l<strlen($text); $l++) {
                $tempkey = substr($key,$intkey, 1);
                //System.out.println("key ke-" + (intkey+1) + ": " + tempkey);

                $temp = substr($text,$l, 1);
                //System.out.println("text ke-" + (l+1) + ": " + temp);

                for($kolom=0; $kolom<10; $kolom++) {
                    if($temp==$value[0][$kolom]) {
                        for($baris=0; $baris<10; $baris++) {
                            if($tempkey==$value[$baris][0]) {
                                $result .= $value[$baris][$kolom];
                                break;
                            }
                        }
                        break;
                    }

                }
                $intkey++;
                if($intkey==strlen($key)) $intkey=0;

            }
            
            return $result;
        }

        public function decrypt($text) {

            $result = "";

            $value = Array();
            for($b=0; $b<10; $b++)
                for($k=0; $k<10; $k++)
                    $value[$b][$k] = ($b+$k>9?$b+$k-10:$b+$k);

            $key = "100476002";
            $intkey = 0;

            for($l=0; $l<strlen($text); $l++) {

                $tempkey = substr($key, $intkey, 1);
                //System.out.println("key ke-" + (intkey+1) + ": " + tempkey);

                $temp = substr($text, $l, 1);
                //System.out.println("text ke-" + (l+1) + ": " + temp);

                for($baris=0; $baris<10; $baris++) {
                    if($tempkey==$value[$baris][0]) { // dapet baris
                        for($kolom=0; $kolom<10; $kolom++) {
                            if($temp==$value[$baris][$kolom]) {
                                $result .= $value[0][$kolom];
                                break;
                            }
                        }
                        break;
                    }
                }
                $intkey++;
                if($intkey==strlen($key)) $intkey=0;

            }
            
            return $result;
        }
        
    }

?>
