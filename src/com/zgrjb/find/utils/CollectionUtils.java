package com.zgrjb.find.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zgrjb.find.bean.MyUser;

import cn.bmob.im.bean.BmobChatUser;

public class CollectionUtils {

	public static boolean isNotNull(Collection<?> collection) {
		if (collection != null && collection.size() > 0) {
			return true;
		}
		return false;
	}
	
	/** list转map
	 *  以用户名为key
	  * @return Map<String,BmobChatUser>
	  * @throws
	  */
	public static Map<String,MyUser> list2maps(List<MyUser> users){
		Map<String,MyUser> friends = new HashMap<String, MyUser>();
		for(MyUser user : users){
			friends.put(user.getUsername(), user);
		}
		return friends;
	}
	
	
	public static Map<String,BmobChatUser> list2map(List<BmobChatUser> users){
		Map<String,BmobChatUser> friends = new HashMap<String, BmobChatUser>();
		for(BmobChatUser user : users){
			friends.put(user.getUsername(), user);
		}
		return friends;
	}
	
	
	
	/** map转list
	  * @Title: map2list
	  * @return List<BmobChatUser>
	  * @throws
	  */
	public static List<MyUser> map2list(Map<String,MyUser> maps){
		List<MyUser> users = new ArrayList<MyUser>();
		Iterator<Entry<String, MyUser>> iterator = maps.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, MyUser> entry = iterator.next();
			users.add(entry.getValue());
		}
		return users;
	}
	
	public static List<BmobChatUser> map2lists(Map<String,BmobChatUser> maps){
		List<BmobChatUser> users = new ArrayList<BmobChatUser>();
		Iterator<Entry<String, BmobChatUser>> iterator = maps.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, BmobChatUser> entry = iterator.next();
			users.add(entry.getValue());
		}
		return users;
	}
}
