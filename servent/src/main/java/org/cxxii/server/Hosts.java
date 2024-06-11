package org.cxxii.server;

public class Hosts {


//    Check existence
//    if none, make one
//    else
//    ping each
//    get pong back
//    if not getting one bac - drop back of the list
//    remove dead hosts if in active for longer than x days



}

/*
*  First time running
* Check host cache for hosts we can ping
* there is none
* ping polaris through http GET
* receive json object os hosts
* save this to host cache (3 hosts)
* ping each of the hosts
* */