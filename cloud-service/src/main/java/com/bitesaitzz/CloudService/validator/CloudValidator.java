package com.bitesaitzz.CloudService.validator;

import com.bitesaitzz.CloudService.models.Cloud;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CloudValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Cloud.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Cloud cloud = (Cloud) target;
        if(cloud.getName().isEmpty() && cloud.getDescription().isEmpty()) {}
    }
}
