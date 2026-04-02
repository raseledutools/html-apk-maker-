package com.rasel.rasfocus;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.Intent;
import android.util.Log;

public class BlockerService extends AccessibilityService {

    // ১. খারাপ কিওয়ার্ডের লিস্ট (বাংলা ও ইংরেজি মিক্সড)
    private final String[] badWords = {
        "sex", "porn", "xvideo", "choti", "xnxx", "mia khalifa", "brazzers", "xxx", "চটি", "যৌন"
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";

        // ২. সেটিংস বা প্লে-স্টোর লক (সেশন চলাকালীন অ্যাপ ডিলিট করা ঠেকাবে)
        if (packageName.equals("com.android.settings") || 
            packageName.equals("com.android.vending")) {
            triggerBlock();
            return;
        }

        // ৩. কন্টেন্ট স্ক্যানার (ব্রাউজারে কী লেখা আছে তা চেক করবে)
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            if (scanContent(nodeInfo)) {
                triggerBlock();
            }
        }
    }

    private boolean scanContent(AccessibilityNodeInfo node) {
        if (node == null) return false;
        
        if (node.getText() != null) {
            String text = node.getText().toString().toLowerCase();
            for (String word : badWords) {
                if (text.contains(word)) return true; // খারাপ কিছু পাওয়া গেছে!
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            if (scanContent(node.getChild(i))) return true;
        }
        return false;
    }

    private void triggerBlock() {
        // হোম স্ক্রিনে পাঠিয়ে দেওয়া
        performGlobalAction(GLOBAL_ACTION_HOME);
        performGlobalAction(GLOBAL_ACTION_BACK);

        // তোমার অ্যাপের সেই "Blocked Page" সরাসরি ওপেন করে দেওয়া
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // অ্যাপকে নির্দেশ দেওয়া যে সরাসরি রেড স্ক্রিন দেখাও
        startActivity(intent);
    }

    @Override public void onInterrupt() {}
}
