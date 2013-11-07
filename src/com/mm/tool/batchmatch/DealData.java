package com.mm.tool.batchmatch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.apache.poi.ss.usermodel.Sheet;

public class DealData {

	public void check(Map<String,Integer> map){
		Iterator<String> keys = (Iterator<String>) map.keySet().iterator();
		int count = 0;
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		while (keys.hasNext()) {
			String selectSpotsById = "select * from st_spots where spotsid = ";
			try{
				Connection c = DatabaseManager.getInstance().getConnection("mysql");
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				try{
					String key = (String) keys.next();
					int spid = NumberConverter.base64toDecimalInt(key);
					selectSpotsById = selectSpotsById + spid;
					ResultSet rs = stmt.executeQuery(selectSpotsById);
					if(rs.next()){

						int mediaId = map.get(key);
						String selectMediaById = "select * from st_media where mediaid=" + mediaId;
						ResultSet rs1 = stmt.executeQuery(selectMediaById);
						if(rs1 != null && rs1.next()){
							String type = rs1.getString("Type");
							rs1.close();
							if("A".equals(type)){
								count++;
								//System.out.println("正常记录数" + count);
							}else{
								count3++;
								System.out.println("投放点："+key + "对应媒体类型不是广告位：" + mediaId);
							}
						}else{
							count2++;
							System.out.println("没查到："+key + "广告位：" + mediaId);
						}
					}else{
						count1++;
						System.out.println("没查到："+key);
					}

					rs.close();
					stmt.close();
					DatabaseManager.getInstance().freeConnection("mysql", c);
				}catch (Exception e) {
					if(stmt != null) {
						try {
							stmt.close();
						}catch(SQLException e2) {
							e2.printStackTrace();
						}
					}
					if(c != null) {
						try {
							DatabaseManager.getInstance().freeConnection("mysql", c);
							c.close();
						}catch(SQLException e1) {
							e1.printStackTrace();
						}
					}
					e.printStackTrace();
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println(count);
		System.out.println(count1);
		System.out.println(count2);
		System.out.println(count3);
	}

	
	public void checkSpotsMatchedDate(Map<String,Integer> map){
		Iterator<String> keys = (Iterator<String>) map.keySet().iterator();
		int count = 0;
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		while (keys.hasNext()) {
			String selectSpotsById = "select * from st_spots where spotsid = ";
			try{
				Connection c = DatabaseManager.getInstance().getConnection("mysql");
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				try{
					String key = (String) keys.next();
					int spid = NumberConverter.base64toDecimalInt(key);
					selectSpotsById = selectSpotsById + spid + " and UpdateDate < '2013-11-05 00:00:00' ";
					ResultSet rs = stmt.executeQuery(selectSpotsById);
					if(rs.next()){
						count++;
						String state = rs.getString("MatchingState");
						int oldMediaId = rs.getInt("MediaId");
						//System.out.println("UpdateDate为今天以前的）："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId + "" + rs.getDate("UpdateDate"));
						if(!"H".equals(state)){
							count1++;
							//System.out.println("没匹配的："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId);
							//System.out.println("UpdateDate为今天以前的未匹配的）："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId + "" + rs.getDate("UpdateDate"));
						}else{
							if(oldMediaId != map.get(key)){
								count2++;
								//System.out.println("没匹配对的："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId);
								//System.out.println("UpdateDate为今天以前匹配的但mediaid不一致的）："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId + "" + rs.getDate("UpdateDate"));
							}else{
								count3++;
								//System.out.println("UpdateDate为今天以前匹配的且mediaid一致的）："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId + "" + rs.getDate("UpdateDate"));
								System.out.println(key + ":" + map.get(key));
							}
						}
					}else{
						//System.out.println("没查到："+key);
					}

					rs.close();
					stmt.close();
					DatabaseManager.getInstance().freeConnection("mysql", c);
				}catch (Exception e) {
					if(stmt != null) {
						try {
							stmt.close();
						}catch(SQLException e2) {
							e2.printStackTrace();
						}
					}
					if(c != null) {
						try {
							DatabaseManager.getInstance().freeConnection("mysql", c);
							c.close();
						}catch(SQLException e1) {
							e1.printStackTrace();
						}
					}
					e.printStackTrace();
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println(count);
		System.out.println(count1);
		System.out.println(count2);
		System.out.println(count3);
	}
	
	public void checkUnMatched(Map<String,Integer> map){
		Iterator<String> keys = (Iterator<String>) map.keySet().iterator();
		int count = 0;
		int count1 = 0;
		while (keys.hasNext()) {
			String selectSpotsById = "select * from st_spots where spotsid = ";
			try{
				Connection c = DatabaseManager.getInstance().getConnection("mysql");
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				try{
					String key = (String) keys.next();
					int spid = NumberConverter.base64toDecimalInt(key);
					selectSpotsById = selectSpotsById + spid;
					ResultSet rs = stmt.executeQuery(selectSpotsById);
					if(rs.next()){
						String state = rs.getString("MatchingState");
						int oldMediaId = rs.getInt("MediaId");
						if(!"H".equals(state)){
							count++;
							System.out.println("没匹配的："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId);
						}else{
							if(oldMediaId != map.get(key)){
								count1++;
								System.out.println("没匹配对的："+ key + ":" + spid +":"+map.get(key) + ":" +oldMediaId);
							}
						}
					}else{
						System.out.println("没查到："+key);
					}

					rs.close();
					stmt.close();
					DatabaseManager.getInstance().freeConnection("mysql", c);
				}catch (Exception e) {
					if(stmt != null) {
						try {
							stmt.close();
						}catch(SQLException e2) {
							e2.printStackTrace();
						}
					}
					if(c != null) {
						try {
							DatabaseManager.getInstance().freeConnection("mysql", c);
							c.close();
						}catch(SQLException e1) {
							e1.printStackTrace();
						}
					}
					e.printStackTrace();
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println(count);
		System.out.println(count1);
	}
	
	public void deal(Map<String, Integer> map) {
		@SuppressWarnings("unchecked")
		Iterator<String> keys = (Iterator<String>) map.keySet().iterator();
		int count = 0;
		while (keys.hasNext()) {
			String selectSpotsById = "select * from st_spots where spotsid = ";
			String updateSpots = "update st_spots set  ";
			try{
				Connection c = DatabaseManager.getInstance().getConnection("mysql");
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				try{
					String key = (String) keys.next();
					int spid = NumberConverter.base64toDecimalInt(key);
					int mediaId = map.get(key);
					selectSpotsById = selectSpotsById + spid;
					ResultSet rs = stmt.executeQuery(selectSpotsById);
					if(rs.next()){
//						int spotsId = rs.getInt("SpotsId");
//						int oldCampaignId = rs.getInt("CampaignId");
						String state = rs.getString("MatchingState");
						int oldMediaId = rs.getInt("MediaId");
						if("H".equals(state)){
							if(mediaId != oldMediaId){
								//(MediaId, MatchingState, MatchedUser, MatchedDate) values
								updateSpots = updateSpots + " MediaId="+mediaId+", MatchingState='H'"+ ", UpdateUser="+ Config.getMatchUserId() + ", UpdateDate=now()"+ " where SpotsId = " + spid;
								stmt.executeUpdate(updateSpots);
								updateMedias(stmt, mediaId, key, "new");
								updateMedias(stmt, oldMediaId, key, "old");
							}
						}else{
							updateSpots = updateSpots + " MediaId="+mediaId+", MatchingState='H'"+ ", UpdateUser="+ Config.getMatchUserId() + ", UpdateDate=now()"+ " where SpotsId = " + spid;
							stmt.executeUpdate(updateSpots);
							updateMedias(stmt, mediaId, key, "new");
						}
					}else{
						System.out.println("没查到："+key);
					}
					rs.close();
					stmt.close();
					c.commit();
					DatabaseManager.getInstance().freeConnection("mysql", c);
				}catch (Exception e) {
					if(stmt != null) {
						try {
							stmt.close();
						}catch(SQLException e2) {
							e2.printStackTrace();
						}
					}
					if(c != null) {
						try {
							c.rollback();
							DatabaseManager.getInstance().freeConnection("mysql", c);
							c.close();
						}catch(SQLException e1) {
							e1.printStackTrace();
						}
					}
					e.printStackTrace();
				}finally {
					if(stmt != null) {
						try {
							stmt.close();
						}catch(SQLException e) {
							e.printStackTrace();
						}
					}
					if(c != null) {
						try{
							DatabaseManager.getInstance().freeConnection("mysql", c);
							//c.close();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			count++;
			if( count % 1000 == 0){
				System.out.println("已完成:"+count);
			}
		}
		DatabaseManager.getInstance().release();
	}
	
	public void updateMedias(Statement stmt, int mediaId, String key, String t) throws SQLException{
		String selectMediaById = "select * from st_media where mediaid=";
		String updateMedia = "update st_media set ";
		
		String selectMediaById1 = selectMediaById + mediaId;
		ResultSet rs1 = stmt.executeQuery(selectMediaById1);
		if(rs1 != null && rs1.next()){
			String path = rs1.getString("Path");
			String type = rs1.getString("Type");
			int matchedSpotsNum1 = rs1.getInt("MatchedSpotsNum");
			rs1.close();
			if("A".equals(type)){
				if(path != null && path.length() > 9){
					String ids[] = path.split("::");
					if(ids.length > 4){
						//::-1::3::4::5:: length:5, maxindex 4
						int maxIndex = ids.length -1;
						int minIndex = 2;
						for(int i = minIndex; i <= maxIndex; i++){
							String selectMediaById2 = selectMediaById + ids[i];
							ResultSet rs = stmt.executeQuery(selectMediaById2);
							if(rs != null && rs.next()){
								int matchedSpotsNum = rs.getInt("MatchedSpotsNum");
								//if("38".equals(ids[i])) System.out.println("38 num:" + matchedSpotsNum);
								if(t.equals("new")){
									//(MatchedSpotsNum, LastMatchSpotsDate) values 
									String updateMedia1 = updateMedia + "MatchedSpotsNum="+ (matchedSpotsNum+1) +", LatestUpdate=now()"+ " where MediaId = " + ids[i];
									stmt.executeUpdate(updateMedia1);
								}else{
									if (matchedSpotsNum > 0) {
										String updateMedia1 = updateMedia + "MatchedSpotsNum="+ (matchedSpotsNum-1) +", LatestUpdate=now()"+ " where MediaId = " + ids[i];
										stmt.executeUpdate(updateMedia1);
									}
								}
							}else{
								System.out.println("没找到广告位：" + mediaId +"父媒体：" + ids[i]);
							}
							rs.close();
						}
					}
				}
				//update p
				if(t.equals("new")){
					//(MatchedSpotsNum, LastMatchSpotsDate) values 
					String updateMedia2 = updateMedia + "MatchedSpotsNum="+ (matchedSpotsNum1+1) +", LatestUpdate=now()"+ " where MediaId = " + mediaId;
					stmt.executeUpdate(updateMedia2);
				}else{
					if (matchedSpotsNum1 > 0) {
						String updateMedia2 = updateMedia + "MatchedSpotsNum="+ (matchedSpotsNum1-1) +", LatestUpdate=now()"+ " where MediaId = " + mediaId;
						stmt.executeUpdate(updateMedia2);
					}
				}
			}else{
				System.out.println("id为：" + mediaId +"的媒体类型不是广告位");
			}
		}else{
			System.out.println("没找到投放点：" + key +"对应的广告位：" + mediaId);
		}
	}
	
	public static void main(String[] args) {
		/*String s = "::adsfas::sdfsdf::";
		System.out.println(s.split("::").length);*/
	}
}
