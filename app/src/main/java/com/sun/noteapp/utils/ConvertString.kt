package com.sun.noteapp.utils

object ConvertString {

    fun actionListToActionDataString(actions: List<String>): String {
        var result = ""
        actions.forEach {
            result += "_0$it"
        }
        result = result.substring(1)
        return result
    }

    fun actionDataStringToActionCheckList(str: String): List<Pair<String, Boolean>> {
        val checkList = mutableListOf<Pair<String, Boolean>>()
        val arrayS = str.split("_")
        arrayS.forEach {
            val check = it[0] == '1'
            checkList.add(Pair(it.substring(1), check))
        }
        return checkList
    }

    fun actionCheckListToActionDataString(checkList: List<Pair<String, Boolean>>): String {
        var result = ""
        checkList.forEach {
            val check = if (it.second) "1" else "0"
            result += "_$check${it.first}"
        }
        result = result.substring(1)
        return result
    }

    fun showActionCheckListByDataString(str: String): String {
        var result = ""
        val arrays = str.split("$UNDER_STROKE")
        arrays.forEach {
            result += "\n+ ${it.substring(1)}"
        }
        return result.substring(1)
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
