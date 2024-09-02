Algoritmer og datastrukturer
Obligatorisk Oppgave 1
Høst 2024 

Laget av 
Kevin Matarewicz
Abdallah Amidu Ndikumana

Applikasjons krav: 

Obligatorisk oppgåve 1: Rekursjon
Denne oppgåva dreier seg om å teikne tre. Dette treet har ei stamme nederst, ut frå denne går to greiner, ein skrått venstre, ein skrått høgre. Ut frå kvar grein går så to nye mindre greiner, til greinene er så små at vi stoppar. Stamme og greiner teiknast berre som tynn strek. Det kan t.d. sjå slik ut:

Lag ein applikasjon med grafisk grensesnitt slik at brukar interaktivt kan styre storleik på treet, vinkel på grein ut frå stamma (forrige grein), storleiken av stamma i høve til resten av treet, m.m. Du skal nytte rekursjon til å teikne treet, og brukar skal kunne styre antal nivå i rekursjonen. Stopp også rekursjonen utfrå storleik, t.d. når greinane er mindre enn to pikslar lange.

I staden for at treet er perfekt, alle greiner sit på plass presis der ein forventar dei (som i figuren over), skal du legge inn litt tilfeldig avvik (random) i vinklar, lengder, og om ei grein i det heile er der:
if (random...) grein( ....);
Dei nederste, største greinene bør ganske sikkert vera på plass, dei minste ytterst kan gjerne mangle. La brukar interaktivt styre ein eller fleire parametre som bestemmer grad av random, frå at alt er på rett plass, til at det er stor variasjon.
