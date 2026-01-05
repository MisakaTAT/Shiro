package com.mikuac.shiro.common.utils;

import java.util.HashMap;
import java.util.Map;

public class FaceUtils {

    protected static final Map<Integer, Integer> EMOJI = new HashMap<>();

    static {
        EMOJI.put(0, 128558);  // 😮 face exhaling
        EMOJI.put(1, 128556);  // 😬 grimacing face
        EMOJI.put(2, 128525);  // 😍 smiling face with heart-eyes
        EMOJI.put(4, 128526);  // 😎 smiling face with sunglasses
        EMOJI.put(5, 128557);  // 😭 loudly crying face
        EMOJI.put(6, 129402);  // 🥺 pleading face
        EMOJI.put(7, 129296);  // 🤐 zipper-mouth face
        EMOJI.put(8, 128554);  // 😪 sleepy face
        EMOJI.put(9, 128557);  // 😭 loudly crying face
        EMOJI.put(10, 128517); // 😅 grinning face with sweat
        EMOJI.put(11, 128545); // 😡 pouting face
        EMOJI.put(12, 128539); // 😛 face with tongue
        EMOJI.put(13, 128513); // 😁 beaming face with smiling eyes
        EMOJI.put(14, 128578); // 🙂 slightly smiling face
        EMOJI.put(15, 128577); // 🙁 slightly frowning face
        EMOJI.put(16, 128526); // 😎 smiling face with sunglasses
        EMOJI.put(19, 129326); // 🤮 face vomiting throw
        EMOJI.put(20, 129325); // 🤭 face with hand over mouth embarrassed
        EMOJI.put(21, 128522); // 😊 smiling face with smiling eyes
        EMOJI.put(23, 128533); // 😕 confused face
        EMOJI.put(24, 128523); // 😋 face savoring food
        EMOJI.put(25, 129393); // 🥱 yawning face
        EMOJI.put(26, 128561); // 😱 face screaming in fear
        EMOJI.put(27, 128531); // 😓 downcast face with sweat
        EMOJI.put(28, 128516); // 😄 grinning face with smiling eyes
        EMOJI.put(29, 128524); // 😌 relieved face
        EMOJI.put(30, 128170); // 💪 flexed biceps
        EMOJI.put(31, 129324); // 🤬 face with symbols on mouth
        EMOJI.put(32, 129300); // 🤔 thinking face question hmmm
        EMOJI.put(33, 129323); // 🤫 shushing face quiet whisper
        EMOJI.put(34, 128565); // 😵 face with crossed-out eyes
        EMOJI.put(35, 128547); // 😣 persevering face
        EMOJI.put(37, 128128); // 💀 skull
        EMOJI.put(38, 128296); // 🔨 hammer
        EMOJI.put(39, 128075); // 👋 waving hand
        EMOJI.put(41, 129398); // 🥶 freezing
        EMOJI.put(42, 128147); // 💓 beating heart
        EMOJI.put(46, 128055); // 🐷 pig face
        EMOJI.put(49, 129303); // 🤗 hugging face
        EMOJI.put(53, 127874); // 🎂 birthday cake
        EMOJI.put(59, 128169); // 💩 pile of poo
        EMOJI.put(60, 9749);   // ☕ hot beverage coffee cup tea
        EMOJI.put(63, 127801); // 🌹 rose flower
        EMOJI.put(66, 10084);  // ❤ mending heart
        EMOJI.put(67, 128148); // 💔 broken heart
        EMOJI.put(69, 127873); // 🎁 wrapped-gift
        EMOJI.put(74, 127774); // 🌞 sun with face
        EMOJI.put(75, 127772); // 🌜 last quarter moon face
        EMOJI.put(76, 128077); // 👍 thumb up
        EMOJI.put(78, 129309); // 🤝 handshake
        EMOJI.put(79, 9996);   // ✌️ victory
        EMOJI.put(85, 128536); // 😘 face throwing a kiss
        EMOJI.put(89, 127817); // 🍉 watermelon
        EMOJI.put(96, 128517); // 😅 grinning face with sweat
        EMOJI.put(99, 128079); // 👏 clapping hands
        EMOJI.put(104, 129393); // 🥱 yawning face
        EMOJI.put(106, 129401); // 🥹 holding back tears
        EMOJI.put(109, 128535); // 😗 kissing face
        EMOJI.put(110, 128562); // 😲 astonished face
        EMOJI.put(111, 129402); // 🥺 pleading face
        EMOJI.put(116, 128536); // 😘 face throwing a kiss
        EMOJI.put(120, 128074); // 👊 fisted hand
        EMOJI.put(122, 128536); // 😘 face throwing a kiss
        EMOJI.put(123, 10060);  // ❌ no
        EMOJI.put(124, 128076); // 👌 ok hand
        EMOJI.put(129, 128587); // 🙋 raising one hand
        EMOJI.put(144, 128079); // 👏 clapping hands
        EMOJI.put(147, 127853); // 🍭 lollipop
        EMOJI.put(171, 127861); // 🍵 tea
        EMOJI.put(172, 128539); // 😛 face with tongue
        EMOJI.put(173, 128557); // 😭 loudly crying face
        EMOJI.put(174, 128558); // 😮‍💨 sigh
        EMOJI.put(175, 128539); // 😛 moe uri
        EMOJI.put(182, 128514); // 😂 face with tears of joy
        EMOJI.put(187, 128123); // 👻 ghost
        EMOJI.put(201, 128077); // 👍 thumb up
        EMOJI.put(214, 128536); // 😘 face throwing a kiss
        EMOJI.put(222, 129303); // 🤗 hugging face
        EMOJI.put(227, 128079); // 👏 clapping hands
        EMOJI.put(247, 128567); // 😷 face with medical mask
        EMOJI.put(264, 129318); // 🤦 dace palm
        EMOJI.put(272, 128579); // 🙃 upside-down face
        EMOJI.put(320, 129395); // 🥳 partying face
        EMOJI.put(325, 128561); // 😱 face screaming in fear
    }

    private FaceUtils() {
    }

    public static int get(int key) {
        return EMOJI.getOrDefault(key, 0);
    }

}