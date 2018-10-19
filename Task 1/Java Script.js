// Closure
(function() {
  /**
   * Decimal adjustment of a number.
   *
   * @param {String}  type  The type of adjustment.
   * @param {Number}  value The number.
   * @param {Integer} exp   The exponent (the 10 logarithm of the adjustment base).
   * @returns {Number} The adjusted value.
   */
  function decimalAdjust(type, value, exp) {
    // If the exp is undefined or zero...
    if (typeof exp === 'undefined' || +exp === 0) {
      return Math[type](value);
    }
    value = +value;
    exp = +exp;
    // If the value is not a number or the exp is not an integer...
    if (value === null || isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) {
      return NaN;
    }
    // Shift
    value = value.toString().split('e');
    value = Math[type](+(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp)));
    // Shift back
    value = value.toString().split('e');
    return +(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp));
  }

  // Decimal round
  if (!Math.round10) {
    Math.round10 = function(value, exp) {
      return decimalAdjust('round', value, exp);
    };
  }
  // Decimal floor
  if (!Math.floor10) {
    Math.floor10 = function(value, exp) {
      return decimalAdjust('floor', value, exp);
    };
  }
  // Decimal ceil
  if (!Math.ceil10) {
    Math.ceil10 = function(value, exp) {
      return decimalAdjust('ceil', value, exp);
    };
  }
})();



/* ********************************************************************
 * Simulate awarding a Bitcoin block
 *  a => Percent of the total network owned by the selfish mining pool
 *  g => If two branches are competing, g (gamma) is the percent of the
 *       honest miners which join the selfish mining pool in mining on
 *       top of the selfish block
 */
function awardBlock(a, g) {
  var rnd = Math.random();

  // Honest miners found a block on top of the selfish block
  if (rnd > a && rnd <= (a + (1 - a) * g)) {

    return 'honest+';

  // Honest miners found a block
  } else if (rnd > (a + (1 - a) * g)) {

    return 'honest';

  // Selfish mining pool found a block
  } else return 'selfish';
}


/* ********************************************************************
 * Selfish mining pool algorithm simulator
 *  Author: Brian Huisman
 *  Based on: Majority is not Enough: Bitcoin Mining is Vulnerable by
 *    Ittay Eyal and Emin Gün Sirer - https://arxiv.org/abs/1311.0243
 */
function calculate() {


  // Pool size of selfish miner
  var alpha = parseFloat(document.getElementById('alpha').value) / 100;

  // The percentage of honest nodes which mine the selfish chain when there's a choice
  var gamma = parseFloat(document.getElementById('gamma').value);

  // Number of iterations to run the simulation
  var loops = parseInt(document.getElementById('loops').value);

  // Run the simulation before (or after) the difficulty adjustment
  var beforeDiff = parseInt(document.querySelector('input[name="beforeDiff"]:checked').value);


  // Effective Network Hashpower (ENH) of each block held back by the selfish miner
  var hiddenBlocks = [];

  // Running tally of found blocks published by the honest miners while behind the selfish mining pool
  var visibleBlocks = 0;

  // Tally number of orphan blocks which get discarded
  var orphanBlocks = 0;


  // Total blocks found by the selfish mining pool
  var selfishBlocks = 0;

  // Effective Network Hashpower (ENH) of each block added to the valid blockchain
  var publicChain = [];



  var x = 1, systemState = 0;
  while (true) {


    // Take action based on the current state of the system
    switch (systemState) {


      // Initial or base state
      // All miners are directing their hashpower towards finding the next block
      case 0:

        // Award a block
        switch (awardBlock(alpha, 0)) {

          // Selfish mining pool finds one block and hides it (a)
          // ENH for this block is equal to the entire network
          case 'selfish':
            hiddenBlocks = [1];
            systemState = 1;
            break;

          // Honest miners find a block (e)
          // ENH for this block is equal to the entire network
          case 'honest':
            publicChain.push(1);
            break;

        }
        break;


      // Selfish mining pool is ahead by at least one hidden block
      case 1:

        // Award a block
        switch (awardBlock(alpha, 0)) {

          // Selfish mining pool pulls further ahead (a)
          // ENH for this block is equal to the selfish mining pool
          case 'selfish':
            hiddenBlocks.push(alpha);
            break;

          // Honest miners find a block
          case 'honest':
            visibleBlocks++;

            // If the selfish mining pool was more than two blocks ahead, it publishes one (h)
            // The oldest block in the competing honest miner chain is invalidated
            // systemState remains the same
            if (hiddenBlocks.length - visibleBlocks > 1) {
              selfishBlocks++;
              orphanBlocks++;

              publicChain.push(hiddenBlocks.shift());
              visibleBlocks--;

            // If the selfish mining pool was just two blocks ahead, it publishes its entire chain (g)
            // All honest miner blocks in the competing branch are invalidated
            // systemState returns to the initial state
            } else if (hiddenBlocks.length - visibleBlocks == 1) {

              if (hiddenBlocks.length > 2) alert(hiddenBlocks.length); // Should never see this

              while (hiddenBlocks.length) {
                selfishBlocks++;

                publicChain.push(hiddenBlocks.shift());
              }
              orphanBlocks += visibleBlocks;
              visibleBlocks = 0;
              systemState = 0;


            // If the selfish mining pool was only one block ahead, it publishes its block and hopes (f)
            // Two blocks will be awarded in systemState -1 (0' from Eyal and Sirer)
            } else if (hiddenBlocks.length == visibleBlocks) {

              if (hiddenBlocks.length != 1) alert(hiddenBlocks.length); // Should never see this

              visibleBlocks = 0;
              systemState = -1;

            } else {

              alert(hiddenBlocks + " :: " + visibleBlocks); // Should never see this

            }
            break;

        }
        break;


      // Special state 0'
      // Two blocks are competing to become the longest chain, one honest and one selfish
      case -1:

        // Award a block - Some honest miners are working on the selfish chain!
        switch (awardBlock(alpha, gamma)) {


          // Selfish mining pool finds the block and wins two blocks (b)
          // Over the past two blocks, ENH1 == ENH of previous block, ENH2 == selfish mining pool + gamma
          // systemState returns to the initial state
          case 'selfish':
            selfishBlocks += 2;

            publicChain.push(hiddenBlocks.shift());
            publicChain.push(alpha + (1 - alpha) * gamma);

            systemState = 0;
            break;


          // Honest miners find the block which extends the selfish chain, each group wins one block (c)
          // Over the past two blocks, ENH1 == ENH of previous block, ENH2 == selfish mining pool + gamma
          // systemState returns to the initial state
          case 'honest+':
            selfishBlocks++;

            publicChain.push(hiddenBlocks.shift());
            publicChain.push(alpha + (1 - alpha) * gamma);

            systemState = 0;
            break;


          // Honest miners find the block which extends their chain and win two blocks (d)
          // Over the past two blocks, ENH1 == honest miners, ENH2 = honest miners - gamma
          // The selfish mining pool LOSES a valid block which it already mined
          // systemState returns to the initial state
          case 'honest':
            hiddenBlocks = [];

            publicChain.push(1 - alpha);
            publicChain.push((1 - alpha) - (1 - alpha) * gamma);

            systemState = 0;
            break;


        }

        // In all cases, one block was orphaned
        orphanBlocks++;

        break;

    }


    // Before difficulty adjustment, allocate X blocks
    if (beforeDiff) {
      if (x >= loops) {

        // If we came from state 0', two blocks were added. Remove the last one
        if (x > loops) publicChain.pop();
        break;
      }

    // After difficulty adjustment, allocate blocks until LOOPS reached
    } else {
      if (publicChain.length >= loops) {

        // If we came from state 0', two blocks were added. Remove the last one
        if (publicChain.length > loops) publicChain.pop();
        break;
      }
    }

    x++;
  }



  // Output the results
  var pchashsum = 0;
  for (var prop in publicChain) pchashsum += parseFloat(publicChain[prop]);
  var enh = Math.round10(pchashsum / publicChain.length * 100, -2);

  var output = [
    ['Selfish miner w/ ' + Math.round10(alpha * 100) + '% hashpower earned:', Math.round10(selfishBlocks / publicChain.length * 100, -2) + '% of blocks'],
    ['Selfish blocks mined / expected if honest:', selfishBlocks + ' / ' + Math.floor(alpha * loops)],
    ['Total blocks mined / expected if honest:', publicChain.length + ' / ' + loops],
    ['Effective Network Hashpower (ENH):', enh + '%'],
    ['Orphaned blocks:', orphanBlocks + ' (' + Math.round10(orphanBlocks / publicChain.length * 100, -2) + '% loss)'],
    ['Difficulty will change to:', Math.round10(publicChain.length / loops * 100, -2) + '%']
  ];

  var dl = document.getElementById('output');

  for (var x = 0, dlih = ''; x < output.length; x++) {
    dlih += '<dt>' + output[x][0] + '</dt>';
    dlih += '<dd>' + output[x][1] + '</dd>';
  }

  dl.innerHTML = dlih;

}



  </script>

</head>
<body>

  <div class="container">
    <section class="section">
      <h3>Selfish Mining &amp; Difficulty Adjustments</h3>
      <h4>The non-intuitiveness of the Selfish Mining scheme</h4>
      <h5>A Javascript Selfish Mining Simulator</h5>

      <p>
        Published: <time>April 9th, 2018</time><br>
        By: Brian Huisman (<a href="https://twitter.com/CryptoWyvern">@CryptoWyvern</a>)
      </p>

      <p>
        <a href="https://arxiv.org/abs/1311.0243">Selfish Mining</a> (SM) is a proposed scheme described in a 2013 paper by Ittay Eyal and Emin Gün Sirer, whereby a dishonest miner with sufficient hashpower on the Bitcoin network can exploit the protocol to mine more blocks than an honest miner with identical hashpower.
      </p>

      <p>
        SM has become a hot topic again recently, since Dr. Craig S. Wright (CSW) came out in a <a href="https://medium.com/@ProfFaustus/the-caner-that-is-the-selfish-mining-fallacy-ed65c20a6ce7">hard line article</a> asserting that it doesn't exist, and the mere idea of it is a 'cancer.'
        The main thrust of CSW's argument seems to be that the SM algorithm "excludes orphans and ignores the time distribution of blocks."
        The simulator in this article attempts to account for this by concentrating on the period of time between when the SM begins to operate, and the time when the mining difficulty changes due to SM's affect on the network.
        The simulator <strong>does not</strong> account for the mathematics and/or algorithm CSW claims better describes the SM situation (or non-situation, as it may be).
        I look forward to reviewing such an algorithm in light of the output of this simulator.
      </p>

      <p>
        A key concept in this simulator is that 'hashpower' is additive.
        A network with X hashpower all working honestly will mine Y blocks in Z time.
        If X is divided into pools, or even down to individual nodes/machines, the entire network will still mine Y blocks in Z time; its <em>potency</em> is not diminished by division into groups.
        If some percentage of X is diverted to mining on a hidden chain, as long as the mining difficulty remains equal for both chains, the entire network will <em>still</em> mine Y blocks in Z time, but due to the nature of hidden chains, a proportion of Y will be discarded as orphans.
        Saying it in a different way, the same number of Y blocks are being mined in Z time, but <em>fewer</em> are ending up in the public, concensus-validated blockchain.
        Ergo, the rate of block generation by the network has decreased, or the Effective Network Hashpower has been reduced.
      </p>

      <p>
        I define <strong>Effective Network Hashpower</strong> (ENH) as hashpower working with the <em>potential</em> to find a block that gets added to the public blockchain.
        Honest Miner (HM) hashpower working on top of a block while the SM is more than a hidden block ahead is effectively 100% wasted via diversion.
        Chain reorganizations mean the ENH associated with orphaned blocks is zero.
        When hashpower is split between two blocks, only the hashpower pointed towards the chain tip where the accepted block gets mined and added is counted towards ENH.
        In this fashion, the ENH of a completely honest chain is never quite 100% because bifurcations do occur rarely.
      </p>

      <p>
        As the simulator shows, one pool adopting a SM strategy has a very large effect on the entire network's ENH.
        Adjust the parameters as you see fit and hit the 'Calculate' button to show the results.
        Since the block-generation function contains a random number generator, hitting 'Calculate' multiple times will show slightly different outcomes each time.
        You can view the source of this page to see the algorithm programmed in JavaScript.
      </p>
    </section>

    <section class="section">
      <form>
        <fieldset>
          <legend style="font-size:150%;background-color:#ffffff;padding:0px 0.5em;">Selfish mining pool simulator</legend>

          <div class="row">
            <div class="col s12 m7">
              <div class="col s12 m12">
                <p class="range-field">
                  <input type="range" id="alpha" min="0.1" max="50" step="0.1" value="35">
                  <label for="alpha">Selfish pool's % of total network hashpower (&alpha;)</label>
                </p>
              </div>
              <div class="col s8 m9">
                <p class="range-field">
                  <input type="range" id="gamma" min="-1" max="1" step="0.01" value="0.5" class="tooltipped" data-position="bottom" data-tooltip="I am a tooltip">
                  <label for="gamma">Gamma (&gamma;)</label>
                </p>
              </div>
              <div class="col s4 m3 input-field">
                <label for="loops">Iterations:</label>
                <input type="number" id="loops" value="2016">
              </div>
              <div class="col m12">
                <p>
                  Gamma (&gamma;) is the proportion of honest miners which mine
                  on the selfish pool's competing block when there is a choice.
                </p>
              </div>
              <div class="col s6 m4">
                Simulate before/after difficulty adjustment:<br><br>
              </div>
              <div class="col s6 m2">
                <label>
                  <input class="with-gap" name="beforeDiff" value="1" type="radio" checked>
                  <span>Before</span><br>
                </label>
                <label>
                  <input class="with-gap" name="beforeDiff" value="0" type="radio">
                  <span>After</span>
                </label>
              </div>
              <div class="col s12 m3 push-m3">
                <button type="button" class="btn waves-effect waves-light" onclick="calculate();">Calculate</button><br><br>
              </div>
            </div>
            <div class="col s12 m5">
              <dl id="output" class="col s12" style="margin-bottom:0px;"></dl>
            </div>
          </div>
        </fieldset>
      </form>

      <p>
        Using the default values for alpha (&alpha; = 0.35) and gamma (&gamma; = 0.5) it can be seen from the simulator that a SM strategy is not <em>initially</em> profitable.
        When a pool begins to SM, the <em>entire network</em> suffers a reduction in efficiency, thereby reducing the block output rate.
        This reduction in block output rate affects <em>both</em> the SM and the honest miners (HM), <em>despite</em> the SM earning more blocks by percentage.
        By numerical quantity, the SM earns fewer blocks in the same period of time than the number of blocks that it <em>would have</em> earned had it used all of its hashpower honestly.
      </p>

      <p>
        In my personal opinion, this is why many people consider the profitability of the SM strategy non-intuitive.
        It would seem the wasted hashpower must mean that the SM strategy is a net loss for all involved, however, this is not so.
      </p>

      <p>
        The unprofitable state described above exists <em>only</em> until the mining difficulty is adjusted downwards to match the loss in efficiency due to SM.
        After the difficulty has been adjusted, the rate of orphan generation and the "time distribution of blocks" are no longer applicable since the rate at which valid blocks are added to the blockchain returns to one every ten minutes on average, despite the reduced ENH.
        Mining gets <em>easier</em> for everyone on the network, HM and SM alike, and this exactly counters the expense of the hashpower wasted on orphaned blocks and futile chains.
      </p>

      <p>
        Therefore SM <em>is</em> a realistic strategy once the difficulty hurdle is passed.
      </p>
      
      <p>
        Under the original Bitcoin Difficulty Adjustment Algorithm (DAA) the SM would need to operate at a loss for up to 2016 blocks (~2 weeks) due to increased block generation time.
        During this period, the HMs would have every incentive to find and block/censor/inhibit the SM by any means possible, as their profitibility would be similarly damaged.
        Once the difficulty is lowered, the SM strategy assumes the profitability exactly as described by Eyal and Sirer.[<a href="#cite-1">1</a>]
        <strong>It's very important to realize that the new dynamic DAA used on the Bitcoin Cash (BCH) network changes this scenario by making a SM strategy profitable almost immediately as the difficulty adjusts downwards quickly to match the efficiency loss of the network.</strong>
      </p>
    </section>

    <section class="section">
      <h5>Negative gamma (&gamma;)</h5>

      <p>
        One of the arguments against the legitimacy of SM is that HMs are incentivized to avoid SM blocks when the blockchain has bifurcated, giving them a choice.
        However, one of the main conclusions of the original paper is that even using a gamma (&gamma;) value of zero, meaning 100% of HMs succeed in avoiding working on the SM block, the SM strategy is still profitable with alpha (&alpha;) at &frac13; and above.
        This is a mindblowing result.
        It shows that a SM with sufficient hashpower doesn't even need to race the propagation of HM blocks in order for the strategy to succeed.
        A "small world" network where every node is connected to every other node, and blocks propagate at the absolute fastest possible speed, is no barrier to the SM strategy.
        A sufficiently large pool can employ it <em>now</em> without any further connectivity changes.
      </p>

      <p>
        Only by making gamma (&gamma;) negative does SMs profitability disappear.
        Yet when the meaning of the value is well-understood, it is obvious that it cannot possibly be negative in this context.
        A negative gamma (&gamma;) would mean that <strong>more</strong> than 100% of HMs are avoiding the SM block.
        It would indicate that even the SM is avoiding mining on its own block, sabotaging its own efforts.
        A properly configured SM pool would find it trivial never to allow such a thing.
      </p>

      <p>
        We can go further to examine the mathematical absurdity of these claims by setting gamma (&gamma;) in the simulator to -1.
        At a gamma (&gamma;) of -1, not only are all the HMs avoiding the SM block, but <em>more</em> than all the SM hashpower has been 'tricked' into mining on top of the HM block as well.
        In other words, more hashpower than actually exists on the network is mining the HM block!
        In this case, a SM with &ge;42% of total hashpower <em><strong>still succeeds</strong></em> in garnering more blocks than their hashpower would normally allow.
      </p>
    </section>

    <section class="section">
      <h5>Include orphans in the DAA?</h5>

      <p>
        When all miners are acting honestly, orphans occur only when two blocks are generated almost simultaneously.
        This happens about once every 60 blocks.[<a href="#cite-2">2</a>]
        However, when one &alpha; = 0.35 mining pool adopts the SM strategy with &gamma; = 0.5, the rate of orphan generation skyrockets to about once every three blocks.
        By this means, while it may be very difficult to know <em>which</em> pool is doing the SM, knowing that SM is going on on the network is trivial.
      </p>

      <p>
        SM can be made perpetually unprofitable for &alpha; &lt; 0.5 by basing the DAA not just on valid blocks added to the chain, but on all valid blocks found, including those that get orphaned.
        This would keep the difficulty from dropping due to a drop in ENH.
        By including orphaned blocks in calculating the DAA, it would become possible to attack the network by slowing down the rate of block generation, but it would <em>never</em> become simultaneously profitable to do so.
      </p>

      <p>
        There are, however, numerous issues with this strategy that preclude it being used without major changes to the protocol, the least of which being that orphans are not propagated to other nodes in the network.
        As well, by incorporating orphan blocks in a calculation of the DAA, the timing of arrival of propagated orphans may cause a fraction of nodes to calculate a different DAA than the rest of the network.
      </p>
    </section>

    <section class="section">
      <h5>Concluding thoughts</h5>

      <p>
        Discussing potential vulnerabilities in the Bitcoin protocol is eminently valuable.
        Satoshi Nakamoto, for all of his prescience, could not possibly have foreseen all the ways in which his invention might be exploited.
        Attributing a sort of divine incorruptibility to the Bitcoin blockchain, and an infallability to Satoshi Nakamoto, is a dangerous mindset that sets the community up for a rude awakening.
      </p>

      <p>
        That being said, what are my personal thoughts on the threat of SM to Bitcoin?
        While I know with absolute certainty that the SM math checks out, I'm confident the probability of it ever being used on the network, while non-zero, is very small.
      </p>

      <p>
        Any mining pool considering the switch to SM must contend with several risk factors that exist above and beyond the Bitcoin protocol itself.
        For instance, it would be quite difficult for a potential SM pool to estimate the gamma (&gamma;) value it would be able to manage before actually beginning to SM.
        This single factor is the difference between needing just a few percent of the hashpower to be profitable and needing 30% or more to do the same.
        An assumption of &gamma; = 0 would be any SM's safest bet, which excludes all but the very largest pools from attempting it profitably.
      </p>

      <p>
        Likewise, because the use of SM vastly increases orphan generation, any outsider could see plainly that SM is taking place on the network and that reward distribution is no longer a fair game.
        As Eyal and Sirer explain, a successful SM scheme incentivizes all other miners to join the SM pool.[<a href="#cite-1">1</a>]
        The threat of this happening would almost certainly cause a decrease in trust of the network, which would inevitably lead to a loss of value.
        Over the long term, a SM scheme is likely to damage the value of any network on which it is used beyond repair.
        Any mining pool considering the SM scheme would recognize this.
      </p>

      <p>
        Generally, this would make the use of SM more desirable for pools with the intent to damage or destroy the network, rather than profit.
        The larger and more valuable the network becomes, it appears less likely SM would be employed as a profit strategy.
        However, the risk of SM remains and we shouldn't willfully blind ourselves to it.
      </p>
    </section>

    <hr>

    <ol>
      <li id="cite-1">Eyal, I., Sirer, E.G.: Majority is not Enough: Bitcoin Mining is Vulnerable. (2013) Retrieved April 3, 2018, from the arXiv database.</li>
      <li id="cite-2">Decker, C., Wattenhofer, R.: Information propagation in the bitcoin network. In: IEEE P2P. (2013)</li>
    </ol>

    <hr>

    <section class="section"><div class="bchaddress">
  <div>
    <strong>Thank you for supporting my work!</strong><br>
    Bitcoin Cash (BCH):<br>
    bitcoincash:qz4svyvsght5jkpt8gvv5n94h2fpm5dryyjzzs0n38 
  </div>
  <div>
    <img src="https://chart.googleapis.com/chart?chs=105x105&amp;cht=qr&amp;chl=bitcoincash:qz4svyvsght5jkpt8gvv5n94h2fpm5dryyjzzs0n38&amp;choe=UTF-8&amp;chld=L|1" title="QR Code" />
  </div>
</div>

<style type="text/css">

.bchaddress {
  height:105px;
  display:table;
  text-align:left;
  margin:0px auto;
}
.bchaddress > div {
  display:table-cell;
  white-space:nowrap;
  vertical-align:middle;
  padding:0.5em;
}
.bchaddress img {
  max-height:105px;
}

@media only screen and (max-width: 600px) {
  .bchaddress > div {
    display:block;
  }
}

</style> 
    </section>
  </div>

  <script src="../../include/materialize.js"></script>
</body>
</html>
