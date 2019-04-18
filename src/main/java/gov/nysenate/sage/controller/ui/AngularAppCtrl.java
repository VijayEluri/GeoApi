package gov.nysenate.sage.controller.ui;

import gov.nysenate.sage.config.Environment;
import gov.nysenate.sage.model.api.ApiRequest;
import gov.nysenate.sage.service.security.ApiKeyLoginToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static gov.nysenate.sage.filter.ApiFilter.getApiRequest;

@Controller
public class AngularAppCtrl {
    private static final Logger logger = LoggerFactory.getLogger(AngularAppCtrl.class);
    private String ipWhitelist;

    @Autowired
    public AngularAppCtrl(Environment env) {
        ipWhitelist = env.getUserIpFilter();
    }

    @RequestMapping({"/"})
    public String home(HttpServletRequest request) {
        String forwardedForIp = request.getHeader("x-forwarded-for");
        String ipAddr= forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        Subject subject = SecurityUtils.getSubject();
        // Senate staff and API users will be routed to the internal dev interface.
        if (subject.isPermitted("ui:view") || ipAddr.matches(ipWhitelist)) {
            return "index";
        }
        return "404";
    }

    @RequestMapping({"/admin"})
    public String adminLogin(HttpServletRequest request) {
        String forwardedForIp = request.getHeader("x-forwarded-for");
        String ipAddr= forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        return "adminlogin";
    }

    @RequestMapping({"/admin/home"})
    public String adminHome(HttpServletRequest request) {
        String forwardedForIp = request.getHeader("x-forwarded-for");
        String ipAddr= forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        return "adminmain";
    }

    @RequestMapping({"/job"})
    public String jobLogin(HttpServletRequest request) {
        String forwardedForIp = request.getHeader("x-forwarded-for");
        String ipAddr= forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        return "joblogin";
    }

    @RequestMapping({"/job/home"})
    public String jobHome(HttpServletRequest request) {
        String forwardedForIp = request.getHeader("x-forwarded-for");
        String ipAddr= forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        return "jobmain";
    }
}