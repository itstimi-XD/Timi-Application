package com.love.timi.exception

class CustomException(val code: Int, val subCode: Int, val msg: String, val data: HashMap<String, Any> = HashMap()): RuntimeException() {

    var outputMessage: String? = null
    /**
     * CustomException으로 생성된 CustomException중 Message에
     * {{}} 부분을 주어진 문자열로 순차적으로 치환하는 함수
     * @param str StringArray 치환할 문자 배열
     * @return Message가 수정된 CustomException
     */
    fun setMsgValue(vararg str: String?): CustomException {
        str.forEach {
            outputMessage = msg.replaceFirst("{{}}", it?: "")
        }
        return this
    }

    fun putData(key: String, value: Any): CustomException {
        data[key] = value
        return this
    }
}