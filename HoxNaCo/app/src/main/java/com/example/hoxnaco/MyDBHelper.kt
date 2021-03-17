package com.example.hoxnaco

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    // 인자 context를 변수로 주려면 val이 붙어야 함
    companion object {  // static 변수 선언
        val DB_VERSION = 1
        val DB_NAME = "myrecord.db"
        val TABLE_NAME = "places"
        val PID = "pid"
        val PADDRESS = "paddress"
        val PDATE = "pdate"
        val PX = "px"
        val PY = "py"
        val PMEMO = "pmemo"
    }

    val myArray = ArrayList<MyPlaces>()

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists "+TABLE_NAME+"("+
                PID + " integer primary key autoincrement, "+
                PADDRESS + " text,"+
                PDATE + " text," +
                PX + " text," +
                PY + " text," +
                PMEMO + " text)"// autoincrement : 자동 증가
        db?.execSQL(create_table)    // db 실행, select 구문 제외 insert, delete 등등 실행 가능
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 버전 정보가 바뀌었을 때
        val drop_table = "drop table if exists "+TABLE_NAME
        db?.execSQL(drop_table) // dp 드랍하고
        onCreate(db)    // 다시 만들기
    }

    fun insertPlace(place :MyPlaces) :Boolean { // INSERT, 삽입 성공 여부
        val values = ContentValues()
        values.put(PADDRESS, place.address)
        values.put(PDATE, place.date)
        values.put(PX, place.x)
        values.put(PY, place.y)
        values.put(PMEMO, place.memo)
        val db=this.writableDatabase    // DB table 객체 획득
        if(db.insert(TABLE_NAME, null, values) > 0) { // insert가 제대로 안 되었을 경우 -1 반환
            db.close()

            val activity = context as Activity
            // 레이아웃 설정해줌
            return true
        }
        else {
            db.close()
            return false
        }
    }

    fun deletePlace(pdate :String) :Boolean {
        val strsql = "select * from " + TABLE_NAME + " where "+
                PDATE + " = \'" + pdate + "\'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.count != 0) { // 무언가를 가지고 옴
            db.delete(TABLE_NAME, "$PDATE =? ", arrayOf(pdate)) // or PIR + " = " + pid
            cursor.close()
            db.close()
            val activity = context as Activity
            // activity 내용 반영
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun updatePlace(place :MyPlaces) :Boolean {  // 내용 수정
        val strsql = "select * from $TABLE_NAME where $PDATE = '${place.date}'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.moveToFirst()) { // 무언가를 가지고 옴
            val values = ContentValues()
            values.put(PMEMO, place.memo)    // 바꾸고자 하는 내용
            db.update(TABLE_NAME, values, PDATE + " =?", arrayOf(place.date))

            cursor.close()
            db.close()

            val activity = context as Activity
            // 
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun getAllRecord() {
        myArray.clear()
        val strsql = "select * from " + TABLE_NAME
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.count != 0) { // 무언가를 가지고 옴
            addRecyClerView(cursor)
        }
        cursor.close()
        db.close()
    }

    private fun addRecyClerView(cursor :Cursor) {   // 리사이클러 뷰에 추가
        cursor.moveToFirst()
        val count = cursor.columnCount
        val myActivity = context as MainActivity
        do {
            myArray.add(MyPlaces(cursor.getString(1), cursor.getString(2), cursor.getString(3),
            cursor.getString(4), cursor.getString(5)))
        } while(cursor.moveToNext())
    }
}