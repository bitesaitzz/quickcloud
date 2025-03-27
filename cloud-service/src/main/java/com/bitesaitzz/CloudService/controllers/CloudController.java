package com.bitesaitzz.CloudService.controllers;


import com.bitesaitzz.CloudService.models.Cloud;
import com.bitesaitzz.CloudService.models.CloudFile;
import com.bitesaitzz.CloudService.models.MessageType;
import com.bitesaitzz.CloudService.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Random;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class CloudController {

   private final CloudService cloudService;
   private final CloudFileService cloudFileService;
   private final RateLimiterService rateLimiterService;
   private final NotificationService notificationService;
    private final UtilService utilService;
    private final Logger logger;
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("cloud", new Cloud());
        return "create";
    }

    @GetMapping("/list")
    public String listClouds(Model model) {
        List<Cloud> clouds = cloudService.listClouds();
        model.addAttribute("clouds", clouds);
        return "clouds";
    }
    @GetMapping()
    public String main(){
        return "main";
    }

    @GetMapping("/code/{code}")
    public String getCloud(@PathVariable String code, HttpServletRequest request, HttpServletResponse response, Model model) {

        UUID userId = utilService.checkAndGenerateUserId(request, response);

        if (!rateLimiterService.isAllowed(userId.toString(), "GET")) {
            model.addAttribute("error", "You have exceeded the limit of requests. Try again later.");
            return "error";
        }
        Cloud cloud = cloudService.getCloudByCode(code);

        if(cloud == null) {
            model.addAttribute("error", "Cloud with the provided code does not exist.");
            return "error";
        }
        List<CloudFile> cloudFiles = cloudFileService.getCloudFilesWithoutUUID(cloud);
        model.addAttribute("cloud", cloud);
        model.addAttribute("cloudFiles", cloudFiles);

        return "cloud";
    }
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {
      return cloudService.downloadFile(fileId);
    }



    @PostMapping()
    public String createCloud(@ModelAttribute Cloud cloud, @RequestParam("files") MultipartFile[] files, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws Exception {
        UUID userId = utilService.checkAndGenerateUserId(request, response);

        if (!rateLimiterService.isAllowed(userId.toString(), "POST")) {
            model.addAttribute("error", "You have exceeded the limit of requests. Try again later.");
            return "error";
        }
        if(files.length == 1 && files[0].getOriginalFilename().isEmpty() && cloud.getDescription().isEmpty() && cloud.getName().isEmpty()) {
            //model.addAttribute("errorMessage", "You must provide at least one file or a description or a name for the cloud.");
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You must provide at least one file or a description or a name for the cloud.");
            return "redirect:/create";
        }

        Cloud savedCloud =  cloudService.createCloud(cloud, files);
        model.addAttribute("cloud", savedCloud);
        model.addAttribute("cloudCode", savedCloud.getCode());
        return "successful_create";
    }


    //for test
    @GetMapping("/random")
    public String randomCloud(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        UUID userId = utilService.checkAndGenerateUserId(request, response);

        if (!rateLimiterService.isAllowed(userId.toString(), "GET")) {
            model.addAttribute("error", "You have exceeded the limit of requests. Try again later.");
            return "error";
        }
        List<String> codes = loadCodesFromFile();
        Random random = new Random();
        String randomCode = codes.get(random.nextInt(codes.size()));
        return getCloud(randomCode, request, response, model);

    }

    //for test
    private List<String> loadCodesFromFile() throws IOException {
        Resource resource = new ClassPathResource("codes.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    @GetMapping("/send-notification")
    public String sendNotification(@RequestParam Map<String, String> params,
                                      Model model) {
        String method = params.get("notificationMethod");
        String email = params.get("email");
        String telegram = params.get("telegram");
        String code = params.get("cloudCode");


        logger.info("\nmethod" + method + " \nemail" + email + "\ntelegram " + telegram + "\ncode " + code);
        if ("email".equals(method) && email != null) {
            logger.info("Sending sendgrid notification");
            notificationService.createAndSendMessage(code, email, MessageType.SENDGRID);
            model.addAttribute("success", "Email sent successfully to "+ email);
        } else if ("telegram".equals(method) && telegram != null) {
            logger.info("Sending telegram notification")
         ; notificationService.createAndSendMessage(code, telegram, MessageType.TELEGRAM);
            model.addAttribute("success", "Telegram message sent successfully to" + telegram);
        } else {
            logger.info("Error: You must provide either email or telegram.");
            model.addAttribute("error", "You must provide either email or telegram.");
        }


        model.addAttribute("cloudCode", code);

        return "successful_create";
    }



    @ExceptionHandler(Exception.class)
    public String handleCloudNotFoundException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }


}
