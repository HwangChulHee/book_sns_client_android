package com.homework.book_sns.javaclass;

import java.util.ArrayList;
import java.util.HashMap;

public class Chatting_roomList_ofClient {
    int user_id;
    HashMap<Integer, ArrayList<Integer>> roomList = new HashMap<Integer, ArrayList<Integer>>(); // user의 보유 채팅방 목록 <room_id, 해당 room의 유저>

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public HashMap<Integer, ArrayList<Integer>> getRoomList() {
        return roomList;
    }

    public void setRoomList(int room_id, int opponent_id) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(opponent_id);
        roomList.put(room_id, arrayList);
    }


}
