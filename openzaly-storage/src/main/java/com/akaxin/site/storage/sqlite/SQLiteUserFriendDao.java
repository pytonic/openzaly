/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.site.storage.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.TimeFormats;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:14:45
 */
public class SQLiteUserFriendDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUserFriendDao.class);
	private static final String USER_FRIEND_TABLE = SQLConst.SITE_USER_FRIEND;
	private static final String USER_PROFILE_TABLE = SQLConst.SITE_USER_PROFILE;
	private static SQLiteUserFriendDao instance = new SQLiteUserFriendDao();

	public static SQLiteUserFriendDao getInstance() {
		return instance;
	}

	// siteUserId -> Friends
	public int queryUserFriendNum(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT COUNT(*) FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=?;";
		int result = 0;

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);

		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			result = rs.getInt(1);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId);
		return result;
	}

	// siteUserId -> Friends
	public List<SimpleUserBean> queryUserFriends(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserBean> userFriendList = new ArrayList<SimpleUserBean>();
		String sql = "SELECT a.site_friend_id,a.alias_name,a.alias_name_in_latin,b.user_name,b.user_name_in_latin,b.user_photo FROM "
				+ USER_FRIEND_TABLE + " AS a LEFT JOIN " + USER_PROFILE_TABLE
				+ " AS b WHERE a.site_friend_id=b.site_user_id AND a.site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			SimpleUserBean bean = new SimpleUserBean();
			bean.setUserId(rs.getString(1));
			bean.setAliasName(rs.getString(2));
			bean.setAliasNameInLatin(rs.getString(3));
			bean.setUserName(rs.getString(4));
			bean.setUserNameInLatin(rs.getString(5));
			bean.setUserPhoto(rs.getString(6));
			userFriendList.add(bean);
		}

		LogUtils.dbDebugLog(logger, startTime, userFriendList.size(), sql, siteUserId);
		return userFriendList;
	}

	// 分页获取用户好友
	public List<SimpleUserBean> queryUserFriendsByPage(String siteUserId, int pageNum, int pageSize)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserBean> userFriendList = new ArrayList<SimpleUserBean>();
		String sql = "SELECT a.site_friend_id,a.alias_name,a.alias_name_in_latin,b.user_name,b.user_name_in_latin,b.user_photo FROM "
				+ USER_FRIEND_TABLE + " AS a LEFT JOIN " + USER_PROFILE_TABLE
				+ " AS b WHERE a.site_friend_id=b.site_user_id AND a.site_user_id=? LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);
		preStatement.setInt(2, startNum);
		preStatement.setInt(3, pageSize);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			SimpleUserBean bean = new SimpleUserBean();
			bean.setUserId(rs.getString(1));
			bean.setAliasName(rs.getString(2));
			bean.setAliasNameInLatin(rs.getString(3));
			bean.setUserName(rs.getString(4));
			bean.setUserNameInLatin(rs.getString(5));
			bean.setUserPhoto(rs.getString(6));
			userFriendList.add(bean);
		}

		LogUtils.dbDebugLog(logger, startTime, userFriendList.size(), sql, siteUserId);
		return userFriendList;
	}

	public boolean saveRelation(String siteUserId, String siteFriendId, int relation) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "INSERT INTO " + USER_FRIEND_TABLE
				+ "(site_user_id,site_friend_id,relation,add_time) VALUES(?,?,?,?);";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);
		preState.setInt(3, relation);
		long currentTime = System.currentTimeMillis();
		preState.setLong(4, currentTime);
		result = preState.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, siteFriendId, relation, currentTime);
		return result > 0;
	}

	public boolean updateRelation(String siteUserId, String siteFriendId, int relation) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + USER_FRIEND_TABLE
				+ " SET relation=?, add_time=? WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setInt(1, relation);
		preState.setLong(2, startTime);
		preState.setString(3, siteUserId);
		preState.setString(4, siteFriendId);
		result = preState.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql + relation, startTime, siteUserId, siteFriendId);
		return result > 0;
	}

	public int queryRelation(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int relation = 0;
		String sql = "SELECT relation FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);
		ResultSet rs = preState.executeQuery();
		if (rs.next()) {
			relation = rs.getInt(1);
		}

		LogUtils.dbDebugLog(logger, startTime, relation, sql + siteUserId, siteFriendId);
		return relation;
	}

	public boolean queryIsFriendRelation(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		boolean relation = false;
		String sql = "SELECT a.relation,b.relation FROM " + USER_FRIEND_TABLE + " AS a INNER JOIN " + USER_FRIEND_TABLE
				+ " AS b ON a.site_user_id=b.site_friend_id AND b.site_user_id=a.site_friend_id where a.site_user_id=? AND a.site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);
		ResultSet rs = preState.executeQuery();
		if (rs.next()) {
			relation = (rs.getInt(1) >= 1 && rs.getInt(2) >= 1);
		}

		LogUtils.dbDebugLog(logger, startTime, relation, sql, siteUserId, siteFriendId);
		return relation;
	}

	public boolean deleteRelation(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);
		int result = preState.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, siteFriendId);
		return result > 0;
	}

	public UserFriendBean queryUserFriendSetting(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT mute FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";

		UserFriendBean bean = null;
		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);

		ResultSet rs = preState.executeQuery();
		if (rs.next()) {
			bean = new UserFriendBean();
			bean.setMute(rs.getBoolean(1));
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, siteUserId, siteFriendId);
		return bean;
	}

	public boolean updateUserFriendSetting(String siteUserId, UserFriendBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_FRIEND_TABLE + " SET mute=? WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setBoolean(1, bean.isMute());
		preState.setString(2, siteUserId);
		preState.setString(3, bean.getSiteUserId());
		int result = preState.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql + siteUserId + "," + bean.toString());
		return result > 0;
	}

	public boolean queryMute(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT mute FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";

		UserFriendBean bean = null;
		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);

		ResultSet rs = preState.executeQuery();
		if (rs.next()) {
			return rs.getBoolean(1);
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql + siteUserId + "," + siteFriendId);

		return true;
	}

	public boolean updateMute(String siteUserId, String siteFriendId, boolean mute) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + USER_FRIEND_TABLE + " SET mute=? WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setBoolean(1, mute);
		preState.setString(2, siteUserId);
		preState.setString(3, siteFriendId);
		result = preState.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, mute, siteUserId, siteFriendId);
		return result > 0;
	}

	public boolean updateFriendAlias(String siteUserId, String siteFriendId, String aliasName, String aliasNameInLatin)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + USER_FRIEND_TABLE
				+ " SET alias_name=?,alias_name_in_latin=? WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, aliasName);
		preState.setString(2, aliasNameInLatin);
		preState.setString(3, siteUserId);
		preState.setString(4, siteFriendId);
		result = preState.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, aliasName, aliasNameInLatin, siteUserId, siteFriendId);
		return result > 0;
	}

	public int getFrienNum(long now, int day) throws SQLException {
		long startTime = System.currentTimeMillis();
		long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
		if (day != 0) {
			endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
		}
		String sql = "SELECT COUNT(*) FROM " + USER_FRIEND_TABLE + " a," + USER_FRIEND_TABLE
				+ " b WHERE a.add_time < ? and a.site_user_id = b.site_friend_id and b.site_user_id = a.site_friend_id ";
		PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preparedStatement.setLong(1, endTimeOfDay);
		ResultSet resultSet = preparedStatement.executeQuery();
		int friendNum = resultSet.getInt(1);
		if (friendNum % 2 != 0) {
			throw new SQLException();
		}
		LogUtils.dbDebugLog(logger, startTime, friendNum, sql);
		return friendNum / 2;
	}

	public boolean delUserFriend(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + USER_FRIEND_TABLE + " WHERE site_user_id= ? or site_friend_id= ?;";
		PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preparedStatement.setString(1, siteUserId);
		preparedStatement.setString(2, siteUserId);
		int i = preparedStatement.executeUpdate();
		if (i > 0) {
			LogUtils.dbDebugLog(logger, startTime, i, sql, "true");
			return true;
		} else {
			LogUtils.dbDebugLog(logger, startTime, i, sql, "false");
			return false;
		}
	}
}
