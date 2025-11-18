import random

# 100 creative, humane sleep reminder quotes
# Mix of supportive and "threatening" (Duolingo-style), all teen-friendly
SLEEP_QUOTES = [
    # Supportive & Encouraging (50)
    "Your brain cells are begging for a reboot. Give them what they deserve!",
    "Even superheroes need sleep. Time to recharge your superpowers!",
    "Tomorrow's success starts with tonight's rest. Let's go!",
    "Sleep isn't lazy - it's strategic. Champions rest well.",
    "Your body has been working hard all day. Time to say thank you with some sleep!",
    "Great minds need great rest. That's you, by the way!",
    "Every hour of sleep is an investment in tomorrow's genius.",
    "Your future self will thank you for sleeping now.",
    "Quality sleep = Quality life. Simple math!",
    "Even your phone needs charging. So do you!",
    "Sleep: the best meditation you can do lying down.",
    "Fajr is waiting for you. Be ready to meet it refreshed!",
    "The early bird catches the worm, but it needs sleep first!",
    "Your dreams are calling. Time to answer!",
    "Rest is not a reward - it's a requirement for greatness.",
    "Sleep well, wake up, dominate!",
    "The most productive thing you can do right now? Sleep.",
    "Your productivity tomorrow depends on your sleep tonight.",
    "Smart people work hard. Wise people also rest hard!",
    "Be kind to yourself. Go to sleep!",
    "Sleep is the golden chain that binds health and our bodies.",
    "The best bridge between despair and hope is a good night's sleep.",
    "You've earned this rest. Take it!",
    "Sleep: because you can't pour from an empty cup.",
    "Your goals are waiting. But first, they need you well-rested!",
    "Every great achievement started with a well-rested mind.",
    "Protect your peace. Protect your sleep.",
    "You deserve rest. No negotiations!",
    "Sleep is self-care in its purest form.",
    "Tomorrow's victories are built on tonight's sleep.",
    "Your brain does amazing things while you sleep. Don't rob it!",
    "Rest is not idleness. It's preparation for greatness!",
    "The night is for rest. The dawn is for conquest!",
    "Sleep well tonight, shine bright tomorrow!",
    "Your body is a temple. Time for temple maintenance!",
    "Missing sleep is like leaving money on the table. Don't do it!",
    "Great students sleep. Great athletes sleep. Great YOU should sleep!",
    "Your immune system works best when you're asleep. Help it out!",
    "Sleep: the original and best performance enhancer.",
    "Tonight's sleep is tomorrow's secret weapon!",
    "Be legendary. Legends sleep well!",
    "Rest now, conquer later!",
    "Sleep like your dreams depend on it. Because they do!",
    "You can't hack sleep. You can only respect it.",
    "Your potential is unlimited. But it needs sleep to unlock!",
    "The world can wait. Your sleep cannot!",
    "Trust the process. The process includes sleep!",
    "Sleep is not time wasted. It's time invested!",
    "Champions rest. Are you a champion or not?",
    "Sweet dreams are made of this: actually going to sleep on time!",

    # Playfully Threatening / Urgent (50)
    "Your sleep schedule is judging you right now.",
    "I'm not mad, just disappointed. But mostly mad. GO TO SLEEP!",
    "Don't make me tell your mom. SLEEP!",
    "Your alarm clock is already plotting revenge. Sleep NOW!",
    "Tomorrow-you is going to be SO mad at today-you. Don't do this!",
    "Sleep now or regret everything tomorrow. Your choice!",
    "I WILL keep sending notifications. Don't test me. SLEEP!",
    "Your grades called. They said go to sleep. NOW.",
    "Do you WANT bags under your eyes? No? THEN SLEEP!",
    "This is your final warning. Well, not final. But SLEEP!",
    "Your brain cells are filing a complaint. Sleep or face the consequences!",
    "I'm trying to help you here. Why do you resist? SLEEP!",
    "Don't make me disable your WiFi. You know I can. Sleep!",
    "Your productivity is about to file for divorce. SLEEP NOW!",
    "Even I need sleep, and I'm just an app. What's YOUR excuse?",
    "Sleep-deprived you is everyone's least favorite you. Don't be that you!",
    "I've seen your face when you don't sleep. The world doesn't need that. SLEEP!",
    "Your future is at stake. No pressure. Just SLEEP!",
    "Are you really going to disappoint your own app? SLEEP!",
    "I track everything. I know you're still awake. SLEEP NOW!",
    "Your circadian rhythm is crying. SLEEP!",
    "Don't be a sleep rebel without a cause. Just SLEEP!",
    "Your pillow is getting cold and lonely. Don't do this to your pillow!",
    "I'm THIS close to calling your alarm clock. Sleep NOW!",
    "Your health insurance doesn't cover sleep deprivation regret. SLEEP!",
    "Every minute you delay is a minute of productivity lost tomorrow. SLEEP!",
    "Your success story doesn't include 'and then they stayed up late for no reason'. SLEEP!",
    "Do you want to be a zombie tomorrow? No? THEN SLEEP!",
    "I'm running out of nice ways to say this. SLEEP. NOW.",
    "Your bed is calling and it sounds ANGRY. Better sleep!",
    "This isn't a suggestion anymore. SLEEP!",
    "Your focus tomorrow will be as sharp as a banana. Unless you SLEEP NOW!",
    "I can do this all night. Can you? No. Because you need SLEEP!",
    "Your willpower tomorrow = 0 without sleep tonight. Math doesn't lie. SLEEP!",
    "Even night owls sleep eventually. Your time is NOW!",
    "Don't make me bring up your browser history. Just SLEEP!",
    "Your dark circles called. They're bringing reinforcements. SLEEP NOW!",
    "I'm not programmed for begging, but here we are. SLEEP!",
    "Tomorrow's regrets start with tonight's decisions. Choose SLEEP!",
    "Your mitochondria are the powerhouse of the cell, but they need sleep. SLEEP!",
    "Science says sleep. Your app says sleep. The universe says sleep. SLEEP!",
    "This could've been an email, but you're still awake, so here we are. SLEEP!",
    "Your body is sending SOS signals. Answer them with SLEEP!",
    "I'll be back in 10 minutes if you're still awake. Don't test me. SLEEP!",
    "Your ancestors slept. Your descendants will sleep. Your turn. NOW!",
    "Plot twist: the secret to success was sleep all along. GO SLEEP!",
    "Your excuses are bad and you should feel bad. SLEEP NOW!",
    "Emergency! Emergency! Just kidding. But seriously, SLEEP!",
    "If you don't sleep, I'm telling your alarm to be EXTRA loud tomorrow!",
    "Last chance. Final offer. Limited time. Just SLEEP already!"
]


def get_random_sleep_quote() -> str:
    """Get a random sleep notification quote"""
    return random.choice(SLEEP_QUOTES)


def get_supportive_quote() -> str:
    """Get a supportive/encouraging quote"""
    return random.choice(SLEEP_QUOTES[:50])


def get_urgent_quote() -> str:
    """Get a playfully threatening/urgent quote"""
    return random.choice(SLEEP_QUOTES[50:])
