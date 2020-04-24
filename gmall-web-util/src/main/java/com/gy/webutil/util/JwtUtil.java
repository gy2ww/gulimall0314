package com.gy.webutil.util;

import io.jsonwebtoken.*;

import java.util.Map;

/**
 * jwt工具类
 * Created by gaoyong on 2020/4/21.
 */
public class JwtUtil {

        public static String encode(String key, Map<String,Object> param, String salt){
            if(salt!=null){
                key+=salt;
            }
            JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);

            jwtBuilder = jwtBuilder.setClaims(param);

            String token = jwtBuilder.compact();
            return token;

        }


        public  static Map<String,Object>  decode(String token ,String key,String salt){
            Claims claims=null;
            if (salt!=null){
                key+=salt;
            }
            try {
                claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            } catch ( JwtException e) {
                return null;
            }
            return  claims;
        }

}
