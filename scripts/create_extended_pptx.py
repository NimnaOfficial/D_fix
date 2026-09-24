import collections
import collections.abc
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE
from pptx.chart.data import CategoryChartData
from pptx.enum.chart import XL_CHART_TYPE, XL_LEGEND_POSITION

def create_extended_presentation():
    prs = Presentation()
    
    # Modern Academic Premium Theme (Light Mode with Navy & Gold)
    BG_COLOR = RGBColor(250, 250, 252)       # Ultra light gray/white
    NAVY_BLUE = RGBColor(10, 37, 64)         # Deep Navy for text/headers
    TEAL_ACCENT = RGBColor(0, 150, 136)      # Teal for highlights
    GOLD_ACCENT = RGBColor(212, 175, 55)     # Academic Gold
    TEXT_MAIN = RGBColor(50, 50, 50)         # Dark gray for body text
    CARD_BG = RGBColor(255, 255, 255)        # Pure white for content cards
    
    def apply_theme(slide):
        bg = slide.background
        fill = bg.fill
        fill.solid()
        fill.fore_color.rgb = BG_COLOR
        
        # Add Footer
        footer = slide.shapes.add_textbox(Inches(0.5), Inches(7.0), Inches(9.0), Inches(0.4))
        tf = footer.text_frame
        p = tf.paragraphs[0]
        p.text = "TechFix: Comprehensive Mobile Device Repair Management System | Academic Project Review 2026"
        p.font.color.rgb = RGBColor(150, 150, 150)
        p.font.size = Pt(10)
        
    def add_title_slide():
        slide = prs.slides.add_slide(prs.slide_layouts[6])
        apply_theme(slide)
        
        # Giant Navy Block
        block = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(2), Inches(10), Inches(3.5))
        block.fill.solid()
        block.fill.fore_color.rgb = NAVY_BLUE
        block.line.fill.background()
        
        # Gold Accent Line
        line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(5.4), Inches(10), Inches(0.1))
        line.fill.solid()
        line.fill.fore_color.rgb = GOLD_ACCENT
        line.line.fill.background()
        
        tf = block.text_frame
        tf.vertical_anchor = MSO_ANCHOR.MIDDLE
        p = tf.paragraphs[0]
        p.text = "TECHFIX ECOSYSTEM"
        p.font.color.rgb = RGBColor(255, 255, 255)
        p.font.size = Pt(60)
        p.font.bold = True
        p.alignment = PP_ALIGN.CENTER
        
        p2 = tf.add_paragraph()
        p2.text = "Extended Technical Architecture & Complete Feature Analysis"
        p2.font.color.rgb = TEAL_ACCENT
        p2.font.size = Pt(24)
        p2.alignment = PP_ALIGN.CENTER
        
    def add_content_slide(title, bullets=None, columns=None, chart_type=None, is_arch=False):
        slide = prs.slides.add_slide(prs.slide_layouts[6])
        apply_theme(slide)
        
        # Header
        hdr = slide.shapes.add_textbox(Inches(0.5), Inches(0.3), Inches(9), Inches(0.8))
        tf = hdr.text_frame
        p = tf.paragraphs[0]
        p.text = title.upper()
        p.font.color.rgb = NAVY_BLUE
        p.font.bold = True
        p.font.size = Pt(32)
        
        # Accent Line
        line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.5), Inches(1.1), Inches(8), Inches(0.05))
        line.fill.solid()
        line.fill.fore_color.rgb = TEAL_ACCENT
        line.line.fill.background()
        
        if is_arch:
            # Draw Architecture Diagram
            comps = [
                ("Android App\n(Java, MVVM)", 0.5, 3.0, TEAL_ACCENT),
                ("Retrofit2 API\n(JSON/REST)", 3.0, 3.0, GOLD_ACCENT),
                ("Cloudflare Workers\n(Edge Network)", 5.5, 2.0, NAVY_BLUE),
                ("Stripe Gateway\n(Payments)", 5.5, 4.0, NAVY_BLUE),
                ("Cloudflare D1\n(SQLite DB)", 8.0, 2.0, TEAL_ACCENT)
            ]
            for txt, x, y, col in comps:
                b = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(x), Inches(y), Inches(1.8), Inches(1.2))
                b.fill.solid()
                b.fill.fore_color.rgb = col
                b.line.fill.background()
                btf = b.text_frame
                btf.text = txt
                btf.paragraphs[0].font.color.rgb = RGBColor(255,255,255)
                btf.paragraphs[0].font.size = Pt(14)
                btf.paragraphs[0].alignment = PP_ALIGN.CENTER
                btf.vertical_anchor = MSO_ANCHOR.MIDDLE
                
            # Add Arrows (just text boxes for simplicity)
            arr1 = slide.shapes.add_textbox(Inches(2.3), Inches(3.2), Inches(0.7), Inches(0.5))
            arr1.text_frame.text = "➔"
            arr1.text_frame.paragraphs[0].font.size = Pt(30)
            
            arr2 = slide.shapes.add_textbox(Inches(4.8), Inches(2.2), Inches(0.7), Inches(0.5))
            arr2.text_frame.text = "➔"
            arr2.text_frame.paragraphs[0].font.size = Pt(30)
            
            arr3 = slide.shapes.add_textbox(Inches(7.3), Inches(2.2), Inches(0.7), Inches(0.5))
            arr3.text_frame.text = "➔"
            arr3.text_frame.paragraphs[0].font.size = Pt(30)
            
        elif columns:
            top = 1.4
            width = 8.5 / len(columns)
            left = 0.5
            for heading, pts in columns.items():
                box = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(left), Inches(top), Inches(width), Inches(5.0))
                box.fill.solid()
                box.fill.fore_color.rgb = CARD_BG
                box.line.color.rgb = RGBColor(220, 220, 220)
                box.shadow.inherit = False
                
                tf_col = box.text_frame
                tf_col.word_wrap = True
                p = tf_col.paragraphs[0]
                p.text = heading
                p.font.color.rgb = NAVY_BLUE
                p.font.bold = True
                p.font.size = Pt(20)
                p.space_after = Pt(14)
                
                for pt in pts:
                    p = tf_col.add_paragraph()
                    p.text = "• " + pt
                    p.font.color.rgb = TEXT_MAIN
                    p.font.size = Pt(16)
                    p.space_after = Pt(10)
                
                left += width + 0.3
                
        elif chart_type:
            # Place chart on right, text on left
            txt_box = slide.shapes.add_textbox(Inches(0.5), Inches(1.5), Inches(4.0), Inches(5.0))
            tf_box = txt_box.text_frame
            tf_box.word_wrap = True
            for pt in bullets:
                p = tf_box.add_paragraph()
                p.text = "• " + pt
                p.font.color.rgb = TEXT_MAIN
                p.font.size = Pt(18)
                p.space_after = Pt(14)
                
            x, y, cx, cy = Inches(4.8), Inches(1.5), Inches(4.5), Inches(4.5)
            if chart_type == "revenue":
                cdata = CategoryChartData()
                cdata.categories = ["Q1", "Q2", "Q3", "Q4"]
                cdata.add_series("Colombo", (12000, 15000, 14000, 18000))
                cdata.add_series("Galle", (8000, 9500, 11000, 13000))
                chart = slide.shapes.add_chart(XL_CHART_TYPE.COLUMN_CLUSTERED, x, y, cx, cy, cdata).chart
                chart.has_legend = True
            elif chart_type == "status":
                cdata = CategoryChartData()
                cdata.categories = ["Completed", "In Progress", "Diagnosing", "Pending Parts"]
                cdata.add_series("Status", (50, 20, 15, 15))
                chart = slide.shapes.add_chart(XL_CHART_TYPE.PIE, x, y, cx, cy, cdata).chart
                chart.has_legend = True
        else:
            # Standard List
            txt_box = slide.shapes.add_textbox(Inches(0.5), Inches(1.5), Inches(9.0), Inches(5.0))
            tf_box = txt_box.text_frame
            tf_box.word_wrap = True
            for pt in bullets:
                p = tf_box.add_paragraph()
                p.text = "• " + pt
                p.font.color.rgb = TEXT_MAIN
                p.font.size = Pt(20)
                p.space_after = Pt(16)

    # 1. Title
    add_title_slide()
    
    # 2. Executive Summary
    add_content_slide("1. Executive Summary", columns={
        "The Vision": [
            "TechFix aims to digitalize the deeply fragmented mobile repair industry.",
            "Eliminates paper trails, manual inventory errors, and poor customer communication.",
            "Provides a highly scalable, real-time platform bridging clients and technicians."
        ],
        "The Ecosystem": [
            "4 distinct user roles: System Admin, Branch Manager, Technician, and Customer.",
            "Native Android application ensures seamless mobile UX.",
            "Edge-deployed backend ensures 0ms latency routing globally."
        ]
    })
    
    # 3. Problem Statement
    add_content_slide("2. Academic Problem Statement", bullets=[
        "Information Silos: Branches traditionally operate independently, making global revenue and inventory tracking impossible for executives.",
        "Customer Blindness: Clients hand over expensive devices with zero visibility into the repair progress until the final bill.",
        "Inefficient Tasking: Technicians waste time querying managers for their next task rather than pulling from an automated, prioritized queue.",
        "Inventory Wastage: Lack of low-stock thresholds leads to halted repairs while waiting for manual spare part procurement."
    ])
    
    # 4. Architecture
    add_content_slide("3. System Architecture Model", is_arch=True)
    
    # 5. Core Technologies
    add_content_slide("4. Core Technologies & Frameworks", columns={
        "Frontend (Android)": [
            "Java / XML Layouts",
            "MVVM (Model-View-ViewModel)",
            "LiveData & StateFlow",
            "Retrofit2 & OkHttp",
            "WorkManager for Offline Sync"
        ],
        "Backend (Edge)": [
            "Cloudflare Workers (Serverless)",
            "Cloudflare D1 (Distributed SQLite)",
            "Custom JWT Middleware",
            "Stripe Payment SDK",
            "Cloudinary API"
        ]
    })
    
    # 6. Database Schema
    add_content_slide("5. Relational Database Schema (ERD)", bullets=[
        "Users & RBAC: Centralized identity mapping users to Customers, Technicians, or Managers based on Enum constraints.",
        "Branches & Inventory: 1:N mapping linking a specific physical branch to thousands of Branch_Spare_Parts.",
        "Appointments Engine: The core table bridging Customer_ID, Technician_ID, Branch_ID, and Status_Enum.",
        "Repair History: Immutable audit trail table logging exactly who changed an appointment state and at what timestamp.",
        "Messages: Polling-based chat table linking Appointment_ID to a list of bidirectional text nodes."
    ])
    
    # 7. Security & Authentication
    add_content_slide("6. Security & Cryptography", bullets=[
        "PBKDF2 Password Hashing: Uses the WebCrypto API with 100,000 iterations and unique salts per user to prevent rainbow table attacks.",
        "JWT Protection: Every REST API route (except login/register) requires a signed Bearer Token validated at the Cloudflare Edge.",
        "Strict Role Boundaries: An API middleware strictly blocks a Technician from hitting a Manager route, returning HTTP 403 (Forbidden).",
        "Atomic Transactions: When assigning a manager to a new branch, SQL transactions ensure they are explicitly removed from their previous branch to prevent orphaned data."
    ])
    
    # 8. Customer Module
    add_content_slide("7. Module: Customer Experience", columns={
        "Device Management": [
            "Register hardware by Brand, Model, and Serial Number.",
            "Edit and maintain a portfolio of devices."
        ],
        "Booking Lifecycle": [
            "Select geographical branch using location data.",
            "Select requested service and preferred date.",
            "Visual stepper tracks: REQUESTED -> DIAGNOSING -> REPAIRING -> COMPLETED."
        ]
    })
    
    # 9. Technician Module
    add_content_slide("8. Module: Technician Workflow", columns={
        "Task Management": [
            "Kanban-style prioritized queue.",
            "Toggle personal availability (AVAILABLE, BUSY, ON_LEAVE).",
            "Technician routing algorithm assigns tasks based on availability and skill."
        ],
        "Diagnostics & Billing": [
            "Upload physical damage photos securely to Cloudinary.",
            "Draft digital quotations by pulling spare parts directly from local branch inventory.",
            "Input custom labor costs and send to customer for digital approval."
        ]
    })
    
    # 10. Manager Module
    add_content_slide("9. Module: Branch Manager", columns={
        "Branch Operations": [
            "Overview of all active, pending, and completed repairs for their specific branch.",
            "Staff oversight: monitor which technicians are currently busy or off-duty."
        ],
        "Inventory Management": [
            "Maintain branch-specific spare parts.",
            "Update stock quantities, define minimum-stock thresholds.",
            "Track parts consumed by specific appointments."
        ]
    })
    
    # 11. Admin Module
    add_content_slide("10. Module: System Administrator", bullets=[
        "God Mode Analytics: Aggregates revenue, active users, and repair counts across the entire country.",
        "Dynamic Branch Creation: Admins can instantiate new physical branches in the database with GPS coordinates and operating hours.",
        "Human Resources: Admins assign Managers to Branches and Technicians to Branches.",
        "System Logging: A dedicated `/api/admin/system/logs` route tracks critical system modifications for security audits."
    ])
    
    # 12. Payments & Stripe
    add_content_slide("11. Secure Payment Gateway (Stripe)", bullets=[
        "Seamless digital checkout replacing cash bottlenecks.",
        "Step 1: Backend generates a secure PaymentIntent summarizing the Quotation cost.",
        "Step 2: Android SDK renders a secure PCI-compliant card entry form.",
        "Step 3: Webhooks confirm payment, automatically transitioning the Appointment state to COMPLETED.",
        "Financial tracking strictly aggregates PAID intents, ignoring PENDING or FAILED attempts."
    ])
    
    # 13. Data Analytics
    add_content_slide("12. Financial Data Analytics", [
        "Advanced SQL queries aggregate cross-branch performance.",
        "Allows executives to identify high-performing geographical regions.",
        "Predicts revenue trends over financial quarters."
    ], chart_type="revenue")
    
    # 14. Status Distributions
    add_content_slide("13. Operational Bottleneck Analysis", [
        "Live status mapping highlights pipeline efficiency.",
        "If 'Diagnosing' exceeds 30%, it indicates a technician shortage.",
        "If 'Pending Parts' exceeds 15%, it triggers an inventory supply-chain review."
    ], chart_type="status")
    
    # 15. Offline & Advanced Android
    add_content_slide("14. Advanced Android Capabilities", bullets=[
        "Offline Resiliency: Uses Room Database to cache branch locations and spare parts so the app opens instantly without network delay.",
        "Background Sync: Implements Android WorkManager to push delayed technician status updates if the Wi-Fi connection drops.",
        "RecyclerView Optimizations: Uses DiffUtil to strictly re-render only the rows in the chat or appointment list that actually changed, saving battery.",
        "Material Design 3: Dynamic coloring, smooth sheet modals, and modern elevation physics."
    ])
    
    # 16. Future Scope
    add_content_slide("15. Future Scope & Research", columns={
        "Artificial Intelligence": [
            "Integrate LLMs to read customer problem descriptions and automatically predict required spare parts before the customer even arrives."
        ],
        "Automated Supply Chain": [
            "When branch inventory drops below threshold, trigger automated purchase orders to global suppliers via API."
        ],
        "Push Notifications": [
            "Implement Firebase Cloud Messaging (FCM) to instantly wake up the user's phone when a repair is finished."
        ]
    })
    
    # 17. Conclusion
    add_content_slide("16. Conclusion", bullets=[
        "TechFix successfully achieves its goal of digitizing the entire mobile repair lifecycle.",
        "The architecture is infinitely scalable due to Cloudflare's serverless edge computing.",
        "The strict separation of concerns (MVVM + RBAC API) ensures a maintainable, enterprise-grade codebase.",
        "Ready for production deployment and real-world execution."
    ])

    prs.save("TechFix_Final_Extended_Presentation.pptx")
    print("Extended Presentation Generated!")

if __name__ == "__main__":
    create_extended_presentation()

