<?php



/*

 * To change this template, choose Tools | Templates

 * and open the template in the editor.

 */



/**

 * Description of GCM

 *

 * @author Ravi Tamada

 */

class GCM {



    //put your code here

    // constructor

    function __construct() {

        

    }



    /**

     * Sending Push Notification

     */

    public function send_notification($registatoin_ids, $message) {

        // include config

        //include_once './config.php';



        // Set POST variables

        $url = 'https://fcm.googleapis.com/fcm/send';



        $fields = array(

            //'time_to_live' => '2419200',

            'delay_while_idle' => false,

            'registration_ids' => $registatoin_ids,

            'data'             => $message

        );



        $headers = array(

            'Authorization: key=AIzaSyB4NyqalpmZeErxyrQXMNDGRjMAUvG3Tts',

            'Content-Type: application/json',

        );

        // Open connection

        $ch = curl_init();



        // Set the url, number of POST vars, POST data

        curl_setopt($ch, CURLOPT_URL, $url);



        curl_setopt($ch, CURLOPT_POST, true);

        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);



        // Disabling SSL Certificate support temporarly

        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);



        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));



        // Execute post

        $result = curl_exec($ch);

        if ($result === FALSE) {

            die('Curl failed: ' . curl_error($ch));

        }



        // Close connection

        curl_close($ch);

        return $result;

    }



}