package com.freelycar.demo.rxkit.demodata

import com.freelycar.demo.rxkit.demodata.base.GenericGenerator
import org.apache.commons.lang3.RandomStringUtils

class EmailAddressGenerator private constructor() : GenericGenerator() {
    override fun generate(): String {
        val result = StringBuilder()
        result.append(RandomStringUtils.randomAlphanumeric(10))
        result.append("@")
        result.append(RandomStringUtils.randomAlphanumeric(5))
        result.append(".")
        result.append(RandomStringUtils.randomAlphanumeric(3))
        return result.toString().toLowerCase()
    }

    companion object {
        val instance: GenericGenerator = EmailAddressGenerator()
    }
}