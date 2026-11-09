package com.example.skybound.data

import com.example.skybound.model.GoalStep
import com.example.skybound.model.GoalTimelineEntry
import org.json.JSONArray
import org.json.JSONObject

object Converters {
    fun parseSteps(json: String): List<GoalStep> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<GoalStep>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GoalStep(
                        title = obj.optString("t", obj.optString("title")),
                        isDone = obj.optBoolean("d", obj.optBoolean("isDone", false))
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun stepsToJson(steps: List<GoalStep>): String {
        val array = JSONArray()
        for (step in steps) {
            val obj = JSONObject()
            obj.put("t", step.title)
            obj.put("d", step.isDone)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseTimeline(json: String): List<GoalTimelineEntry> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<GoalTimelineEntry>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GoalTimelineEntry(
                        timestamp = obj.optLong("t", System.currentTimeMillis()),
                        description = obj.optString("x", obj.optString("description"))
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun timelineToJson(timeline: List<GoalTimelineEntry>): String {
        val array = JSONArray()
        for (entry in timeline) {
            val obj = JSONObject()
            obj.put("t", entry.timestamp)
            obj.put("x", entry.description)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseStringList(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun stringListToJson(list: List<String>): String {
        val array = JSONArray()
        for (item in list) {
            array.put(item)
        }
        return array.toString()
    }
}
