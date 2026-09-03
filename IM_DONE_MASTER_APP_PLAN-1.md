# I'M DONE!

## Master App Plan

**Created for Team LALLI61**  
**App type:** Kids' chores, routines, reminders and rewards  
**Devices:** Android phones and tablets, iPhone and iPad

---

## The Main Idea

I'M DONE! is a simple family app that reminds children to complete everyday jobs such as brushing their teeth, making their bed, packing their school bag and cleaning their room.

The app becomes the little digital pain-in-the-arse that reminds them, so Mum and Dad do not have to ask them 50 times.

It must be quick, colourful and extremely easy for children to use. The child mainly needs to see what must be done and press **ALL DONE** when the job is finished.

---

## 1. Parent Sets Up Each Child

The parent creates a separate profile for each child.

Each profile can have:

- Child's name
- Profile picture or avatar
- Favourite colour or profile theme
- Their own phone or iPad connected to the family
- Their own list of chores and routines
- Their own points and rewards

The parent controls all important settings. Children cannot create rewards, change point values or mark chores as approved by a parent.

---

## 2. Parent Creates the Jobs

The parent creates each chore or routine and chooses when it must happen.

Example daily setup:

| Time | Job | Points |
| --- | --- | ---: |
| 7:00 AM | Brush teeth | 1 |
| 7:30 AM | Make bed | 1 |
| 7:45 AM | Pack school bag | 1 |
| 4:30 PM | Clean room | 3 |
| 5:00 PM | Feed the dogs | 2 |
| 7:00 PM | Have a shower | 1 |
| 8:00 PM | Brush teeth | 1 |

For every job, the parent can choose:

- Job name
- Simple icon
- Time
- Points or stars earned
- Every day
- Weekdays only
- Weekends only
- Selected days of the week
- One-time job
- Whether the job needs parent approval
- How long before the reminder repeats

---

## 3. The Child Gets Reminded

At the selected time, the child's phone or iPad sends a notification.

Example:

> 🪥 **Time to brush your teeth!**

The child opens the reminder, completes the job and presses:

## ALL DONE ✅

If the child ignores the notification, I'M DONE! reminds them again after a time chosen by the parent, such as 10 minutes.

The reminder should keep working even when the app is closed.

---

## 4. Simple Child Home Screen

The child's home screen must stay very simple.

Example:

## GOOD MORNING, DYLAN 👋

| Today's job | Status |
| --- | --- |
| 🪥 Brush teeth | **DONE ✅** |
| 🛏️ Make bed | **DONE ✅** |
| 🎒 Pack school bag | **DONE ✅** |
| 🧹 Clean room | **TO DO** |

**⭐ 7 points today**

The child should immediately understand:

- What must be done now
- What is coming next
- What is already finished
- How many points they earned
- How close they are to their next reward

There should be no complicated menus on the child screen.

---

## 5. Parent Dashboard

The parent can see every child's progress from one screen.

Example:

## Dylan — Today

- ✅ Brush teeth
- ✅ Make bed
- ❌ Clean room
- ⏳ Feed the dogs

The parent dashboard allows Mum or Dad to:

- See completed, missed and upcoming jobs
- Add, edit, pause or remove jobs
- Change times and repeat days
- Set reminder repeat times
- Choose point values
- Approve jobs that require checking
- Create and control rewards
- See daily and weekly progress
- Manage every child's device and profile

---

## 6. Points and Rewards

Children earn stars or points when they complete their jobs.

Example points:

- ⭐ Brush teeth = 1 point
- ⭐ Make bed = 1 point
- ⭐⭐ Help with dishes = 2 points
- ⭐⭐⭐ Clean room = 3 points

Example rewards:

- 20 ⭐ = 30 minutes extra screen time
- 50 ⭐ = Choose Friday dinner
- 100 ⭐ = $5 pocket money

The parent creates the rewards and decides how many points each reward costs.

When a child has enough points, they can request a reward. The parent must approve it before the points are used.

---

## 7. Important App Rules

- Parents control chores, schedules, points and rewards.
- Children cannot secretly change jobs or point values.
- A child can press **DONE**, but selected jobs can require parent approval.
- Missed jobs remain clearly marked instead of disappearing.
- Notifications must work when the app is closed.
- Each child sees only their own simple screen.
- Parents can manage all children from one parent account.
- The app must be safe for children and collect as little personal information as possible.
- No advertising should appear on the child's screen.

---

## 8. First Build — Must-Have Features

The first working version should include:

1. Parent account and protected parent area
2. Child profiles with name, avatar and colour
3. Create, edit and delete chores
4. Daily, weekday, weekend and selected-day schedules
5. Notifications at the selected time
6. Repeat reminder if ignored
7. Large **DONE** button
8. Today's child checklist
9. Parent progress dashboard
10. Points and stars
11. Parent-created rewards
12. Parent approval for selected chores and rewards
13. Android phone and tablet support
14. iPhone and iPad support

---

## 9. Possible Later Features

These can be considered after the main app works properly:

- Voice reminders using the parent's recorded voice
- Family competitions or shared goals
- Streaks for completing routines several days in a row
- Bonus jobs that children can choose
- School-day and holiday schedules
- Different reminder sounds for each child
- Photo proof for selected jobs
- Weekly family progress report
- Celebration animation when all jobs are finished
- Temporary pause for sickness, holidays or weekends away

These features must not make the main child screen complicated.

---

## 10. Core App Promise

**I'M DONE! helps children remember their everyday jobs, build good routines and earn rewards — without Mum and Dad having to repeat themselves all day.**

---

## Confirmed Direction

This document is the base plan for I'M DONE! The app should remain simple for children and give parents full control over chores, reminders, progress, points and rewards.

---

## Confirmed Working Prototype — 2 September 2026

A working phone prototype has been created and tested at:

https://im-done-kids.tilewithralf.chatgpt.site

The prototype currently includes:

- Child home screen with colourful, playful job cards
- Big **ALL DONE ✅** button on every job
- Stars awarded when jobs are completed
- Progress bar and running star total
- Celebration after all jobs are completed
- Parent screen opened from the settings cog
- Editable chore titles
- Editable star value for every chore
- Parent-defined reward name
- Parent-defined number of stars needed for the reward
- Add unlimited new chores
- Delete chores
- Reset today's completed jobs
- Changes saved on the current device

Confirmed starter chores:

1. Brush My Teeth
2. Make My Bed
3. Lunch Box
4. Pack My School Bag
5. Behave In School
6. Clean My Room
7. Feed The Dogs
8. Rubbish Out
9. Lawn Done

## Real App Build Direction

- Build Android first while keeping the project compatible with iPhone and iPad.
- Use one master project and one source of truth.
- Test every feature on a real Android phone before moving to the next feature.
- Keep the child screen simple, colourful and easy for small children.
- Keep all setup and control inside the protected parent area.
- Add real scheduled notifications that work when the app is closed.
- Preserve chores, stars, rewards and progress after the app is restarted.
- **Confirmed choice:** Build for connected parent and child devices.
- Mum or Dad controls chores, stars and rewards from the parent phone.
- The parent can add and manage multiple children inside the one parent app.
- Each child has their own name, avatar, colour, chores, stars, progress and rewards.
- The parent dashboard shows every child and lets the parent open one child's details at a time.
- Each child receives their chores and reminders on their own phone or iPad.
- A child joins the family using a simple family code and is linked to the child profile selected by the parent.
- The parent area is protected so children cannot change chores, stars or rewards.
- Completed chores sync back to the parent's dashboard.
- Save development work through GitHub before moving to iOS work on the MacBook.

**Team LALLI61**
