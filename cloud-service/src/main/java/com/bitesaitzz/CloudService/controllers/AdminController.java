package com.bitesaitzz.CloudService.controllers;


import com.bitesaitzz.CloudService.models.Cloud;
import com.bitesaitzz.CloudService.models.CloudFile;
import com.bitesaitzz.CloudService.services.CloudFileService;
import com.bitesaitzz.CloudService.services.CloudService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CloudService cloudService;
    private final CloudFileService cloudFileService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/clouds")
    public String clouds(Model model) {
        List<Cloud> clouds = cloudService.listClouds();
        model.addAttribute("clouds", clouds);
        return "clouds";
    }

    @GetMapping("/clouds/update/{id}")
    public String updateCloud(@PathVariable Long id, Model model) {
        Cloud cloud = cloudService.getCloudById(id);
        if (cloud == null) {
            model.addAttribute("error", "Cloud with the provided id does not exist.");
            return "error";
        }
        List<CloudFile> cloudFiles = cloudFileService.getCloudFilesWithoutUUID(cloud);
        model.addAttribute("cloud", cloud);
        model.addAttribute("cloudFiles", cloudFiles);
        return "update";
    }

    @PostMapping("/clouds/update/{id}")
    public String updateCloud(@PathVariable Long id,
                              @ModelAttribute Cloud cloud,
                              @RequestParam("files") MultipartFile[] files) {
        cloudService.updateCloud(id, cloud, files);
        return "redirect:/admin/clouds";
    }

    @PostMapping("/clouds/delete/{id}")
    public String deleteCloud(@PathVariable Long id) {
        cloudService.deleteCloud(id);
        return "redirect:/admin/clouds";
    }

}
