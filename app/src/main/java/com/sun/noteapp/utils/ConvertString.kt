package com.sun.noteapp.utils

import com.sun.noteapp.data.model.ToDo

object ConvertString {

    fun convertToDoDataStringToList(dataString: String): List<ToDo> =
        if (dataString.isEmpty()) {
            emptyList()
        } else {
            dataString.split("$UNDER_STROKE").map { item: String ->
                ToDo(
                    item.substring(1),
                    item.first() == '1'
                )
            }
        }

    fun convertToDoListToDataString(toDos: List<ToDo>): String {
        var result = ""
        val stringBuilder = StringBuilder()
        if (toDos.isNotEmpty()) {
            toDos.forEach {
                val isComplete = if (it.isComplete) "1" else "0"
                stringBuilder.append("$UNDER_STROKE$isComplete${it.name}")
            }
            result = stringBuilder.toString().substring(1)
        }
        return result
    }

    fun showActionCheckListByDataString(dataString: String): String {
        var result = ""
        val stringBuilder = StringBuilder()
        if (dataString.isNotEmpty()) {
            val arrays = dataString.split("$UNDER_STROKE")
            arrays.forEach {
                stringBuilder.append("\n+ ${it.substring(1)}")
            }
            result = stringBuilder.toString().substring(1)
        }
        return result
    }

    fun labelListToLabelStringData(strings: List<String>): String {
        var result = ""
        strings.forEach {
            result += "_$it"
        }
        return result.substring(1)
    }

    fun labelStringDataToLabelList(string: String): List<String> {
        return string.split("$UNDER_STROKE")
    }
}
