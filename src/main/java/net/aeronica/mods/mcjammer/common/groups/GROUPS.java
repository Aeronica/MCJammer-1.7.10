package net.aeronica.mods.mcjammer.common.groups;

import java.util.Map;

import com.google.common.base.Splitter;

public enum GROUPS {
	GROUP_ADD, MEMBER_ADD, MEMBER_REMOVE, MEMBER_PROMOTE, QUEUED, PLAYING;

	public static final int MAX_MEMBERS = 8;

	// Client side
	public static Map<String, String> clientGroups;
	public static Map<String, String> clientMembers;
	public static Map<String, String> clientPlayStatuses;

	public static String getLeaderOfGroup(String groupID) {
		if (GROUPS.clientGroups != null) {
			return GROUPS.clientGroups.get(groupID);
		}
		return null;
	}

	public static String getMembersGroupID(String memberName) {
		if (GROUPS.clientMembers != null) {
			return GROUPS.clientMembers.get(memberName);
		}
		return null;
	}

	public static boolean isLeader(String memberName) {
		return memberName
				.equalsIgnoreCase(getLeaderOfGroup(getMembersGroupID(memberName)));
	}
	
	/**
	 * getIndex(String playerName)
	 * 
	 * This is used to return a the index for the playing status icons
	 * @param playerName
	 * @return int
	 */
	public static int getIndex(String playerName) {
		int result = 0;
		if (GROUPS.clientPlayStatuses != null
				&& GROUPS.clientPlayStatuses.containsKey(playerName)) {
			switch (GROUPS.valueOf(GROUPS.clientPlayStatuses.get(playerName))) {
			case QUEUED:
				result = 1;
				break;
			case PLAYING:
				result = 2;
			default:
			}
		}
		return result + (GROUPS.isLeader(playerName) ? 8 : 0);
	}

	public static Map<String, String> splitToHashMap(String in) {
		try {
			return (Map<String, String>) Splitter.on(" ")
					.withKeyValueSeparator("=").split(in);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
