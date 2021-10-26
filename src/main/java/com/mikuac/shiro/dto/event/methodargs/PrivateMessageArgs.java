package com.mikuac.shiro.dto.event.methodargs;

import lombok.*;

import java.util.Set;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessageArgs {

    Set<Object> messageArgs;
}
