library(lattice)

strategy = c('honest', 'honest', 'honest', 'honest', 'honest', 'selfish mining', 'selfish mining', 'selfish mining', 'selfish mining', 'selfish mining', 'L', 'L', 'L', 'L', 'L', 'equal-fork-stubborn', 'equal-fork-stubborn', 'equal-fork-stubborn', 'equal-fork-stubborn', 'equal-fork-stubborn', 'T1', 'T1', 'T1', 'T1', 'T1', 'L, F', 'L, F', 'L, F', 'L, F', 'L, F', 'L, T1', 'L, T1', 'L, T1', 'L, T1', 'L, T1', 'F, T1', 'F, T1', 'F, T1', 'F, T1', 'F, T1', 'L, F, T1', 'L, F, T1', 'L, F, T1', 'L, F, T1', 'L, F, T1')
selfish_share                 = c(.15, .225, .3, .375, .45, .15, .225, .3, .375, .45, .15, .225, .3, .375, .45, .15, .225, .3, .375, .45, .15, .225, .3, .375, .45, .15, .225, .3, .375, .45, .15, .225, .3, .375, .45, .15, .225, .3, .375, .45, .15, .225, .3, .375, .45)
accepted_blocks_honest        = c(1836 * .85, 1836 * .775, 1836 * .7, 1836 * .625, 1836 * .55, 1536, 1350, 1116, 919, 624, 1538, 1350, 1126, 931, 648, 1542, 1356, 1120, 921, 597, 1534, 1350, 1119, 921, 648, 1543, 1362, 1160, 981, 733, 1537, 1353, 1131, 922, 648, 1540, 1353, 1119, 911, 613, 1545, 1358, 1160, 1000, 720)
accepted_blocks_selfish_miner = c(1836 * .15, 1836 * .225, 1836 * .3, 1836 * .375, 1836 * .450, 78, 166, 328, 442, 601, 79, 160, 301, 415, 538, 70, 152, 314, 417, 597, 81, 163, 325, 441, 575, 65, 136, 254, 306, 375, 77, 153, 302, 420, 537, 68, 151, 317, 429, 581, 62, 138, 255, 294, 389)
stale_blocks_honest           = c(93 * .85, 93 * .775, 93 * .7, 93 * .625, 93 * .55, 108, 149, 232, 299, 446, 106, 149, 222, 287, 422, 102, 143, 228, 297, 473, 110, 149, 229, 297, 422, 101, 137, 188, 237, 337, 107, 146, 217, 296, 422, 104, 146, 229, 307, 457, 99, 141, 188, 218, 350)
stale_blocks_selfish_miner    = c(93 * .15, 93 * .225, 93 * .3, 93 * .375, 93 * .45, 193, 237, 231, 271, 249, 192, 243, 258, 298, 312, 201, 251, 245, 296, 253, 190, 240, 234, 272, 275, 206, 267, 305, 407, 475, 194, 250, 257, 293, 313, 203, 252, 242, 284, 269, 209, 265, 304, 419, 461)

df = data.frame(selfish_share, strategy, accepted_blocks_honest, accepted_blocks_selfish_miner, stale_blocks_honest, stale_blocks_selfish_miner)

cols <- c('red', 'blue', 'black')

df = df[- grep("L", df$strategy),]
df = df[- grep("T1", df$strategy),]
df <- droplevels(df)

mykey <- list(space = 'top',
              columns = 2,
              text = list(levels(df$strategy)),
              points = list(pch = 1, col = cols)
) 

df$selfish_mined_share = df$accepted_blocks_selfish_miner/(df$accepted_blocks_selfish_miner + df$accepted_blocks_honest)
xyplot(selfish_mined_share ~ selfish_share, df, type = 'b', groups = factor(df$strategy), asp = 1, col = cols,
       ylab = 'Relative gain [%]', xlab = 'Computational share [%]',
       scales=list(
         y=list(
           at=selfish_share
         ),
         x=list(
           at=selfish_share
         )
       ),
       key = mykey
)

