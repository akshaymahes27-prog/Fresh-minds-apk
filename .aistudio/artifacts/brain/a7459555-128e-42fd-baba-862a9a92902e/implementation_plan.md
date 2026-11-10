# Skybound Artist Specification & Design Brief

Comprehensive asset guide and style specification for the Skybound artist. This document defines all required mascot poses, interchangeable parts, activity icons, altitude shields, color palettes, and technical export standards for the TeenTech competition entry.

---

## Art Style Directive: Duolingo-Inspired Chunky 3D Vector

### 1. Visual DNA & Geometry
- **Chunky & Friendly**: Large, clean, rounded silhouette shapes with minimal visual clutter. Avoid thin, spindly lines or hyper-detailed textures.
- **The "Duolingo" 3D Bevel Base**: Every major button, card, and icon uses a 2-tone depth effect: a vibrant primary fill on top, resting on a darker, slightly thicker base rim (typically 3–4dp bottom offset) giving it a satisfying, tactile, toy-like feel.
- **Clean Solid Colors**: Flat cel-shading or subtle 2-stop linear gradients (top-to-bottom only). No complex airbrushing or realistic lighting.
- **Expressive Faces**: Big white oval eyes with friendly dark-navy pupils, white specular highlights, and expressive eyebrows/beaks that convey clear emotion in small thumbnail sizes (down to 32×32dp).

### 2. Core Color Palette & Hex References

| Token Name | Hex Code | Purpose & Application |
| :--- | :--- | :--- |
| **Sky Blue (Primary)** | `#1CB0F6` | Primary mascot body, main action buttons, focus highlights |
| **Deep Sky (3D Base)** | `#1899D6` | 3D underside bevel for Sky Blue elements, pressed button states |
| **Sun Gold (Accent)** | `#FFC800` | Mascot head crest, trophy badges, milestone stars, rank celebrations |
| **Sun Gold Base** | `#E5B400` | 3D underside bevel for Gold elements |
| **Coral Red** | `#FF6B6B` | High priority tags, active attention dots, sport presets |
| **Coral Base** | `#E05353` | 3D underside bevel for Coral elements |
| **Mint Green** | `#3DDC97` | Completed goal checkmarks, success states, rest day badges |
| **Mint Base** | `#28C27F` | 3D underside bevel for Mint elements |
| **Violet** | `#A78BFA` | Reflection & review prompts, session tags, coach feedback |
| **Violet Base** | `#8B5CF6` | 3D underside bevel for Violet elements |
| **Flame Orange** | `#FF9F43` | Weekly rhythm streaks, habit counters, mascot beak |
| **Flame Base** | `#E0852A` | 3D underside bevel for Orange elements |
| **Deep Navy (Dark text)** | `#0F2A4A` | Eye pupils, text on bright buttons, high-contrast outlines |
| **Sky Cloud (Card light)**| `#EAF6FF` | Light mode card backgrounds, subtle pill tags |
| **Dark Card (Card dark)** | `#132233` | Dark mode card backgrounds |

---

## 1. Mascot Pose Specifications (Pip the Skybound Bird)

All poses should feature **Pip**, Skybound's friendly, supportive bird mascot. Pip has a rounded egg-like body, a golden crest feather, a warm orange beak, and small expressive wings.

### Core Character Sheet: 12 Expressive Poses

1. **`wave` (Greeting & Top Bar Mascot)**
   - **Expression**: Big happy open smile, head tilted 5° playfully.
   - **Pose**: Left wing resting on hip/belly, right wing raised high waving forward.
   - **Emotion**: Welcoming, warm, inviting.
   - **Usage**: Top navigation bar, Welcome header, Tour introduction.

2. **`hatchling` (Beginner Zero-State)**
   - **Expression**: Wide curious eyes looking upward, tiny timid smile.
   - **Pose**: Sitting snugly inside a cracked pastel-blue eggshell with tiny golden crest poking out.
   - **Emotion**: Beginner starting fresh, zero expectations, pure potential.
   - **Usage**: Altitude Tier 1 ("Hatchling"), new user onboarding, empty activity state.

3. **`cheer` (Milestone & Rhythm Streak)**
   - **Expression**: Eyes closed in happy curved arcs (`^ ^`), beak open wide in joy.
   - **Pose**: Both wings thrown into the air, hopping off the ground with celebratory speed lines.
   - **Emotion**: Enthusiastic celebration, pride in effort.
   - **Usage**: Weekly rhythm target achieved, goal finished, celebration popup.

4. **`focus` (Current Aim / Pre-Session Cue)**
   - **Expression**: Determined, confident, focused eyes with a subtle game-face eyebrow.
   - **Pose**: Standing firmly on two orange feet, wearing a red athletic sweatband on head, small clipboard or focused wing pointing forward.
   - **Emotion**: Ready to train, locked in, supportive.
   - **Usage**: "Current Aim" focus card, pre-session notification banner.

5. **`meditate` (Rest Day & Reflection)**
   - **Expression**: Peaceful closed eyes, serene gentle smile, calm aura.
   - **Pose**: Sitting cross-legged with wings resting gently on knees, subtle glowing halo or soft zen cloud beneath.
   - **Emotion**: Calm, guilt-free rest, steady mindful habit.
   - **Usage**: Post-session reflection screen, rest day confirmation, quiet hours.

6. **`flying_launcher` (The Skybound Soar / Speed Mode)**
   - **Expression**: Thrilled, wide open eyes, streamlined wind-swept cheeks.
   - **Pose**: Flying horizontally forward at a 30° upward climb, wings tucked back like a jet, twin sonic wind streaks and a cloud puff trailing behind.
   - **Emotion**: Momentum, breakthrough, soaring high.
   - **Usage**: Mascot Studio launcher mode, altitude rank-up celebration, splash sequence.

7. **`trophy` (Personal Best & All-Time Record)**
   - **Expression**: Beaming with golden sparkles in eyes, proud puffed-out chest.
   - **Pose**: Standing tall holding a chunky golden trophy cup with `#1` or a star embossed on the front.
   - **Emotion**: Mastery, pride in personal record.
   - **Usage**: Records & Milestones screen, new Personal Best unlocked.

8. **`tuning` / `coach_feedback` (Feedback Analysis)**
   - **Expression**: Thoughtful, curious, one eyebrow raised with a pencil tucked behind crest.
   - **Pose**: Holding a mini magnifying glass or checklist notebook, leaning forward inquisitively.
   - **Emotion**: constructive learning, coach advice into action.
   - **Usage**: "Record Coach Feedback" screen, Goal detail tuning.

9. **`proud_shield` (Altitude Level Up)**
   - **Expression**: Heroic stance, confident wink.
   - **Pose**: Standing beside a large chunky medieval heraldic shield bearing the Skybound wing insignia.
   - **Emotion**: Accomplishment, protection of consistent training.
   - **Usage**: Altitude level-up dialog, TeenTech judging feature overview.

10. **`sleepy` (Quiet Hours & Off-Season)**
    - **Expression**: Eyes closed in peaceful sleep, soft `zzz` floating up.
    - **Pose**: Curled into a snug feathery ball wearing a cute nightcap, resting on a soft cloud pillow.
    - **Emotion**: Guilt-free recovery, rest is part of training.
    - **Usage**: Quiet Hours toggle active (21:00 - 08:00), Rest day review.

11. **`confetti_party` (Major Tier Graduation)**
    - **Expression**: Ecstatic open beak, party blower in mouth.
    - **Pose**: Mid-air jump with colorful rectangular paper confetti falling all around.
    - **Emotion**: Pure celebration of major achievement.
    - **Usage**: Goal milestone unlock dialog, TeenTech final tour step.

12. **`rocket_hop` (Habit Streak Hit)**
    - **Expression**: Goggle-wearing adventurous grin.
    - **Pose**: Riding a small playful cartoon rocket with soft smoke rings underneath.
    - **Emotion**: Unstoppable rhythm, fun momentum.
    - **Usage**: Habit tracker completion, 3/3 weekly rhythm hit.

---

## 2. Modular Mascot System: Interchangeable Component Parts

For the in-app **Mascot Studio** (where users can customize Pip in real-time), provide separate asset layers so each piece can be swapped independently:

### A. Head Crests (Attach to top of head)
- **Tuft**: Default soft 3-feather golden plume (`#FFC800`).
- **Crown**: Chunky 3-point royal gold crown with a red jewel in the center.
- **Headband / Sweatband**: Athletic red band (`#FF6B6B`) with white center stripe.
- **Flame Crest**: Three fiery stylized feathers fading from orange to yellow.
- **Goggles**: Brown aviator pilot goggles strapped around the forehead.

### B. Eye Expressions (Centered on face)
- **Friendly (Default)**: Large white ovals with dark navy pupils and white specular glint.
- **Curved Happy (`^ ^`)**: Cheerful closed arcs for success states.
- **Wink**: Left eye open wide, right eye in an energetic wink arc.
- **Sunglasses**: Sleek black cartoon retro sunglasses with white corner glare.
- **Star Eyes**: Big golden stars glowing in pupils for record-breaking excitement.

### C. Beaks & Expressions
- **Happy Open Beak**: Bright orange triangular wedge open in cheerful laughter (`#FF9F43`).
- **Gentle Smile**: Closed curved golden beak showing warm encouragement.
- **Determined Smirk**: Slightly tilted upward beak showing focus.

### D. Wings
- **Folded / Hip**: Tucked neatly against the body for calm standing.
- **Waving**: Raised high in welcoming greeting.
- **Spread Flying**: Extended horizontally for soaring and aerodynamics.
- **Thumbs Up / Wing Tip**: Pointed upward with a cute feathery tip.

### E. Accessories (Attachable overlay layer)
- **None**: Clean, classic Pip.
- **Golden Medal**: Gold ribbon necklace with embossed star medal.
- **Hero Cape**: Flowing red superhero cape flapping gently behind.
- **Jetpack**: Dual sky-blue rocket canisters with subtle flame puffs.
- **Champion Belt**: Heavy gold boxing/wrestling buckle with wing insignia.

---

## 3. Handcrafted Activity Icons (No Generic Letter Badges!)

Each activity icon represents a youth training discipline. The style must be **chunky vector inside a rounded squircle badge (16dp corner radius)** with thick 2.5dp contours and high-contrast colors:

| Activity Icon Key | Visual Subject | Dominant Color | 3D Base Color | Meaning & Usage |
| :--- | :--- | :--- | :--- | :--- |
| **`ball`** | Soccer / Football ball with clean chunky pentagon patches | Coral Red `#FF6B6B` | `#E05353` | Traditional sports, ball games, pitch drills |
| **`note`** | Two eighth notes beamed together with a cheerful star | Sky Blue `#1CB0F6` | `#1899D6` | Music practice, instruments, vocal training |
| **`dumbbell`** | Chunky curved dumbbell weight with shine mark | Mint Green `#3DDC97` | `#28C27F` | Fitness, gym, athletics, strength training |
| **`dance`** | Pair of stylized ballet pointe slippers with ribbon arcs | Soft Pink `#FF7EB6` | `#E0609A` | Dance, gymnastics, figure skating, movement |
| **`book`** | Open chunky hardcover book with bookmark ribbon | Violet `#A78BFA` | `#8B5CF6` | Academics, studying, language learning, reading |
| **`palette`** | Artist wooden palette with 3 paint spots & brush | Sun Gold `#FFC800` | `#E5B400` | Visual arts, digital design, drawing, craft |
| **`target`** | Circular bullseye with arrow in dead center | Flame Orange `#FF9F43` | `#E0852A` | General focus goals, archery, shooting, precision |
| **`heart`** | Bold plump heart with pulse sparkle | Crimson `#FA5252` | `#D63333` | Wellbeing, mindfulness, recovery, mental health |
| **`star`** | Rounded 5-point star with 3D drop depth | Golden Amber `#F59F00` | `#D97706` | Personal bests, general milestones, favorites |
| **`repeat`** | Two chunky circular recycling/loop arrows | Teal `#20C997` | `#12B886` | Daily habits, routines, weekly rhythms |

---

## 4. Altitude Progression Shields (10 Ranks)

Skybound replaces streak guilt with an **Altitude System**. As athletes log completed goals, their altitude rank climbs from sea level to the summit. Each shield should have a distinct heraldic silhouette:

1. **`hatchling` (Rank 1 · 0 goals)**: Soft round wooden egg-shaped buckler with light wood grain.
2. **`fledgling` (Rank 2 · 1 goal)**: Simple bronze triangular shield with a single feather emblem.
3. **`flutter` (Rank 3 · 3 goals)**: Bronze kite shield with two small wing notches.
4. **`glider` (Rank 4 · 6 goals)**: Polished silver heater shield with a clean wingspan crest.
5. **`thermal` (Rank 5 · 10 goals)**: Silver shield with golden sunburst rays radiating outward.
6. **`ascender` (Rank 6 · 15 goals)**: Gold-trimmed steel shield with upward wind vortices.
7. **`stratus` (Rank 7 · 22 goals)**: Cloud-embossed cobalt blue shield with dual silver stars.
8. **`cloudburst` (Rank 8 · 30 goals)**: Deep navy shield with golden lightning and rain sparkles.
9. **`soarer` (Rank 9 · 40 goals)**: Radiant golden shield with outstretched eagle/falcon wings.
10. **`summit` (Rank 10 · 50+ goals)**: Mythical crystalline platinum shield crowned with a radiant summit peak and mountain halo.

---

## 5. Technical Specifications for the Artist

- **Base Resolution**: Master art created at **1024×1024 px** on a square canvas.
- **Export Formats**:
  1. Transparent **PNG-32** (lossless) at:
     - `xxxhdpi`: 512×512 px
     - `xxhdpi`: 384×384 px
     - `xhdpi`: 256×256 px
     - `mdpi`: 128×128 px
  2. **Vector SVG** (or Adobe Illustrator / Figma source file) with merged compound paths for direct import into Android Studio as `VectorDrawable` XML.
- **Safe Margins**: Keep all character and icon artwork inside an **85% safe boundary** (leave 7.5% transparent padding on all four edges so circular clipping in launcher icons, notifications, and chips never crops ears, beaks, or wings).
- **Stroke / Contours**: If outlines are used, maintain a uniform stroke weight (8–10% of canvas width) with rounded line caps and rounded line joins (`stroke-linecap="round"`).

---

## 6. Delivery Checklist for Artist

- [ ] Pip Character Sheet (Neutral, 3/4 turn, front view)
- [ ] 12 Expression Poses (Wave, Hatchling, Cheer, Focus, Meditate, Flying Launcher, Trophy, Tuning, Proud Shield, Sleepy, Confetti, Rocket Hop)
- [ ] 5 Interchangeable Head Crests (Tuft, Crown, Headband, Flame, Goggles)
- [ ] 5 Interchangeable Accessories (None, Medal, Cape, Jetpack, Belt)
- [ ] 10 Handcrafted Activity Icons (Ball, Note, Dumbbell, Dance, Book, Palette, Target, Heart, Star, Repeat)
- [ ] 10 Altitude Progression Shields (Hatchling to Summit)
- [ ] Vector source file (.SVG or .FIG) with named layers
