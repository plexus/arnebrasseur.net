---
title: Audio Compression for the Rest of Us
date: 2013-01-10
---

It's the onset of summer, and tomorrow you'll be trotting off with your bff's to the biggest bestest music festival in decades. Full of anticipation you drift into sleep... and suddenly find yourself transported to the festival grounds, the main act is just about to take the stage, and you're the one behind the mixing desk, making sure they sound *amazing* (p.s. this is a dream).

The thing is, you have a problem. The singer has an amazing voice, and she uses the full range of it. Not only from low to high notes (the frequency range), but also from really really quiet, to really really loud (the dynamic range). In the meanwhile the rest of the band is producing this steady wall of sound, so when her singing gets close to a whisper there's just no way anyone will hear her. And when the chorus starts and she starts screaming and hopping around the sound of the band vanishes into the background.

So with a sense of duty you grab hold of the fader that controls the volume of her voice. You set the baseline volume loud enough so her whispers can be heard, and every time she gets really loud you pull the fader down a little bit, and then back up afterwards.

You're basically trying to reduce her "very very loud" into "very loud", and her "very loud" into "just loud". But when she's singing quiet, or normal, anything less than very loud, you do nothing.

So you're standing there, bobbing that fader up and down, trying to track her musical escapades, but you're always a little too late and the whole thing just sounds horrible. If only a machine could do what you were trying to do.

And then you notice this device in the rack on your right. It says "dynamic range compressor" in shiny silver letters at the top right, it has a couple of knobs you can turn, and two rows of colored leds. The first row is labeled "input volume", and you can see that it lights up in sync with her singing. It seems like the device is tracking the volume of her voice.

The second row of leds is labeled "gain reduction", but they don't light up. It seems there's something the machine could be doing but isn't.

"Maybe", you think to yourself, "gain reduction" just means "reducing the volume by this much", and I can make this machine do what I'm trying to do manually.

The first knob is labeled "threshold", and it has markings going from 0dB all the way to ∞dB (you know those are decibells, i.e. a measure of how loud something is). It's currently all the way to the right, at "infinite loudness". Seems like it's time to lower the bar.

So you start turning the knob to the left, and around 120dB you notice that the "gain reduction" leds start lighting up just a little bit when her singing gets really loud. You keep your ears cocked and keep turning. And amazed you find out that IT IS WORKING. Each time she goes "very very loud" the machine turns it into "very loud" just like you were trying to do before.

In other words, it's bringing loud and quiet closer together. If her singing was like a picture of mountains, with the high peeks representing the really loud singing, what you've done is you've pressed top part of the picture together a bit. So the tops of the highest mountains are a little less high, but the rest of the hills haven't changed.

The second knob on the machine is labeled ratio, and it's at 2:1. What this means is that every two decibels over the threshold will be halved. You can turn it up to 3:1 or 5:1 to get more compression. You're still only changing the highest peaks, but you're pressing them together a little harder.

## Other parameters

I'll quickly summarize these

### Attack and release

Sound is a very organic thing, it typically goes up and down gradually. If we all of sudden decrease the volume when a certain threshold is reached it might sound a bit weird. The "attack" (a time in ms) is the time it takes the compressor to go from zero compression to the configured compression ratio (like 2:1).

Release is the opposite, it's the time it takes the compressor to stop compressing after the sound has dropped below the threshold again.

### Make up gain

Sine a compressor makes the loudest parts a bit more quiet, it actually makes the whole signal on average a bit more quiet. To compensate many compressors have a "make up gain" setting, it's basically just an extra amplifier to boost the volume a bit after compression.

## Special types of compressors

### Expander

If the ratio is e.g. 1:2 instead of 2:1, then 1 decibel over the threshold will be turned into 2, so it will make the loudest parts louder. This is called an expander. Think of it as a reverse compressor.

### Limiter

Rather than reducing the volume in par with actual signal, a limiter will simply stop the signal from exceeding the threshold. It's like setting an infinite ratio. As soon as the threshold is reached, any excess volume is squashed to nothing.

### Normalization

With digital music there is a fixed volume range to operate in, you can't go louder than 100%. Digital volume is measured in the dBFS scale, where 0 means maximum volume, anything less is a negative number.

If the loudest part of a recording is at -30dBFS, then that's 30dB of dynamic range you're not using. Normalization means automatically figuring out compression/expansion settings so you are using the full scale

## Closing thoughts

Ever noticed that when TV commercials start the volume suddenly jumps up? Technically they are not louder than the rest of the broadcast, it's all been normalized to use the full range of whatever medium is being used to transmit the audio.

However the commercials will have had very hard compression applied to them, so instead of quiet and loud, there's only loud and loud. It's a trick to catch your attention. In some places there is regulation to limit this "loudness war".
