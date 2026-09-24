import re

with open("app/src/main/java/com/mad/techfix/ui/payment/PaymentFragment.java", "r", encoding="utf-8") as f:
    content = f.read()

import_pattern = r"(import com\.stripe\.android\.Stripe;)"
import_repl = r"\1\nimport com.stripe.android.PaymentConfiguration;"

content = re.sub(import_pattern, import_repl, content)

oncreate_pattern = r"(@Nullable\s*@Override\s*public View onCreateView)"
oncreate_repl = """@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaymentConfiguration.init(requireContext(), StripeConfig.PUBLISHABLE_KEY);
    }

    \\1"""

content = re.sub(oncreate_pattern, oncreate_repl, content)

with open("app/src/main/java/com/mad/techfix/ui/payment/PaymentFragment.java", "w", encoding="utf-8") as f:
    f.write(content)

print("Fixed PaymentFragment.")

