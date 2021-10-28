package com.mikuac.shiro.dto.action.common;

import com.mikuac.shiro.enums.MethodEnum;
import lombok.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HandlerMethodCollection {


    private MultiValueMap <MethodEnum, Method> privateMethods= new LinkedMultiValueMap<>();



}
