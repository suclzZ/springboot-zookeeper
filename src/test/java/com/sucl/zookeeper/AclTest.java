package com.sucl.zookeeper;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 理解ACL原理与如何配置节点ACL！
 * ACL
 *  创建节点时设置ACL
 *  scheme:默认认证方式：
 *      world：默认方式，相当于全世界都能访问,id为anyone
 *      auth：代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户)，没有id
 *      digest：即用户名:密码这种方式认证，这也是业务系统中最常用的,setAcl <path> digest:<user>:<password(密文)>:<acl>
 *              第二密码是经过sha1及base64处理的密文
 *              echo -n <user>:<password> | openssl dgst -binary -sha1 | openssl base64
 *      host：根据地质认证
 *      ip：使用Ip地址认证
 *  id:
 *      world、anyone：
 *      auth、""：
 *      具体用户名:密码、host、ip
 *  perms:CREATE、READ、WRITE、DELETE、ADMIN 也就是 增、删、改、查、管理权限(rwadc)
 *      CREATE: create
 *      READ: getData getChildren
 *      WRITE: setData
 *      DELETE: delete
 *      ADMIN: setAcl addauth
 *  cli:权限配置scheme:id:perm
 *      world ： world:anyone:rwadc
 *      auth ：只需要认证即可访问 ，由zookeeper密文处理
 *          > addauth digest zookeeper:zookeeper
 *          > setAcl /auth auth:zookeeper:zookeeper:rwadc
 *      digest ：用户名: 密码先进行sha1编码后再用base64编码,明文编码
 *          > setAcl /digest digest:zookeeper:zookeeper:rwadc
 *
 *          > echo -n zookeeper:zookeeper | openssl dgst -binary -sha1 | openssl base64
 *          > setAcl /digest digest:zookeeper:[pwd->sha1->base64]:rwadc
 *      host ：可通过后缀匹配，abc.com可以匹配 www.xyz.abc.com
 *      ip ：addr/bits eg:192.168.1.1, 192.168.0.0/16,192.168.*.*
 *
 * @author sucl
 * @date 2019/6/3
 */
public class AclTest {

    public void test1() throws NoSuchAlgorithmException {
        ZooKeeper zk = ZookeeperConnection.zooKeeper;
//        zk.addAuthInfo();

        //world
        List<ACL> acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;

        //auth
        new ACL(ZooDefs.Perms.ALL,new Id("auth","username:password"));

        //digest
        ACL aclDigest = new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest("username:password")));

        //host
        new ACL(ZooDefs.Perms.ALL,new Id("host","www.scl.com"));

        //ip
        new ACL(ZooDefs.Perms.ALL,new Id("ip","192.168.1.1/16"));//匹配前16位
    }
}
