package com.love.timi.controller

import com.love.timi.properties.LoveProperties
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/user-auth")
class ProjectController {

    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    @ResponseBody
    fun userAuth(): String {
        return """
            <H2>Admin-User-Service</H2>
            <b>Build</b> : ${LoveProperties.Project.build}<br/>
            <b>Version</b> : ${LoveProperties.Project.version}<br/>
            <b>Update date</b> : ${LoveProperties.Project.date}<br/>
        """
    }
}