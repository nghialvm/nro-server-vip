<?php
class ACB
{
    public $clientId = '';
    private $URL = array(
        "LOGIN" => "https://apiapp.acb.com.vn/mb/v2/auth/tokens",
        "getBalance" => "https://apiapp.acb.com.vn/mb/legacy/ss/cs/bankservice/transfers/list/account-payment",
        "GET_TRANS" => "https://apiapp.acb.com.vn/mb/legacy/ss/cs/bankservice/saving/tx-history?maxRows=20&account=4650511",
    );
    function __construct($username, $password)
    {
        $this->username = $username;
        $this->password = $password;
    }
    public function login()
    {
        $header = array(
            'Content-Type: application/json; charset=utf-8',
            'Host: apiapp.acb.com.vn',
        );
        $data = '{
            "clientId" : "'.$this->clientId.'",
            "username" : "'.$this->username.'",
            "password" : "'.$this->password.'"
        }';
        return $this->CURL("LOGIN", $header, $data);
    }
    public function get_balance($token) {
        $header = array (
            'Content-Type: application/json; charset=utf-8',
            'Host: apiapp.acb.com.vn',
            "Authorization: bearer $token",
        );
        $result = $this->CURL("getBalance", $header,$data = null);
        return json_encode($result);
    }
    public function LSGD($accountNo,$rows,$token) {
        $header = array (
            'Content-Type: application/json;',
            'Host: apiapp.acb.com.vn',
            "Authorization: bearer $token",
            'User-Agent: ACB-MBA/5 CFNetwork/1333.0.4 Darwin/21.5.0',
            'Accept-Language: vi',
            'x-app-version: 3.7.0'
        );
        $result = $this->CURL2("https://apiapp.acb.com.vn/mb/legacy/ss/cs/bankservice/saving/tx-history?maxRows=".$rows."&account=".$accountNo."", $header,$data = null);
        return json_encode($result);
    }
    public function curl_get($url,$header)
    {
        $proxy = '103.162.31.109:3694';
        $proxyauth = 'sun53694:1whmx';
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_PROXY, $proxy);
        curl_setopt($ch, CURLOPT_PROXYUSERPWD, $proxyauth);
         curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        $data = curl_exec($ch);

        curl_close($ch);
        return $data;
    }
    public function CURL2($Action, $header, $data)
    {
        $proxy = '103.162.31.109:3694';
        $proxyauth = 'sun53694:1whmx';
        $Data = is_array($data) ? json_encode($data) : $data;
        $curl = curl_init();
        $opt = array(
            CURLOPT_URL => $Action,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POST => empty($data) ? false : true,
            CURLOPT_POSTFIELDS => $Data,
            CURLOPT_CUSTOMREQUEST => empty($data) ? 'GET' : 'POST',
            CURLOPT_HTTPHEADER => $header,
            CURLOPT_ENCODING => "",
            CURLOPT_HEADER => false,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_2,
            CURLOPT_TIMEOUT => 20,
        );
        curl_setopt_array($curl, $opt);
        $body = curl_exec($curl);
        if (is_object(json_decode($body))) {
            return json_decode($body, true);
        }
        return $body;
    }

    private function CURL($Action, $header, $data)
    {
        $Data = is_array($data) ? json_encode($data) : $data;
        $curl = curl_init();
        $header[] = 'Content-Type: application/json; charset=utf-8';
        $header[] = 'accept: application/json';
        $header[] = 'Content-Length: ' . strlen($Data);
        $opt = array(
            CURLOPT_URL => $this->URL[$Action],
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POST => empty($data) ? false : true,
            CURLOPT_POSTFIELDS => $Data,
            CURLOPT_CUSTOMREQUEST => empty($data) ? 'GET' : 'POST',
            CURLOPT_HTTPHEADER => $header,
            CURLOPT_ENCODING => "",
            CURLOPT_HEADER => false,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_TIMEOUT => 20,
        );
        curl_setopt_array($curl, $opt);
        $body = curl_exec($curl);
        if (is_object(json_decode($body))) {
            return json_decode($body, true);
        }
        return json_decode($body, true);
    }
    public function generateImei()
    {
        return $this->generateRandomString(8) . '-' . $this->generateRandomString(4) . '-' . $this->generateRandomString(4) . '-' . $this->generateRandomString(4) . '-' . $this->generateRandomString(12);
    }
    public function generateRandomString($length = 20)
    {
        $characters = '0123456789zxcvbnmlkjhgfdsaqwertyuiopZXCVBNMLKJHGFDSAQWERTYUIOP';
        $charactersLength = strlen($characters);
        $randomString = '';
        for ($i = 0; $i < $length; $i++) {
            $randomString .= $characters[rand(0, $charactersLength - 1)];
        }
        return $randomString;
    }
    public function get_TOKEN()
    {
        return $this->generateRandomString(39);
    }
}