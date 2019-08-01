# my-animemes-bot
My first discord bot. Add some SubReddits and let the Bot print some Reddit posts into your Discord server.

Used [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA)

build / run jar:
https://stackoverflow.com/questions/33613857/noclassdeffounderror-while-executing-main-class-using-java-classpath-command/33616594#33616594

___

1. [Installation](#installation)
2. [Commands](#commands)
3. [List of commands](#list-of-commands)

## Installation
1) Create a new Discord Bot [here](https://discordapp.com/developers/applications/)
    - Create a new Application
    - Bot > Add Bot
    - You need the token of the bot (not application) later in step **4**

2) Add the Bot to your Discordserver, more info [here](https://discordapp.com/developers/docs/topics/oauth2#bots)
    - client_id: Client ID of your application ('General Information')
    - scope: 'bot'
    - permissions: Send Messages, Embed Links, Read Message History => 83968
    - Open URL in browser: ```https://discordapp.com/api/oauth2/authorize?client_id=[YOUR_CLIENT_ID]&scope=bot&permissions=83968```
    
3) Get access to the Reddit API, more info [here](https://github.com/reddit-archive/reddit/wiki/OAuth2)
    - Create a new App: https://old.reddit.com/prefs/apps/
        - Set name
        - Choose 'script'
        - redirect uri = 'http://localhost'
    - You need the id and secret of you app later in step **4**
4) Install and configure your bot
    - [Download and install maven](https://maven.apache.org/download.cgi)
    - ``git clone https://github.com/Ocin007/my-animemes-bot.git``
    - go to ``my-animemes-bot\src\main\resources``
    - Rename ``config.properties.example`` to ``config.properties``
    - Open ``config.properties`` and set the following values:
        - botToken: token from step **1**
        - defaultChannelId: id of the channel used when the bot starts (greeting when bot starts)
            - settings > appearance > developer mode
            - right click on channel > copy ID
        - clientId, clientSecret: from step **3**
        - redditUsername: your Reddit Username, without 'u/'
        - platform: for example 'windows', 'android', ...
    - *optional: you can configure the prefixes, commands and most of the output messages in the ``.properties`` files*
    - inside of ``my-animemes-bot`` directory:
        - ``mvn package``
        - ``java -cp target\my-animemes-bot-1.0-SNAPSHOT.jar de.ocin007.Bot``
    - Now your bot should go online and greet you.
5) Authorize the bot to use the reddit api
    - send following command as private message to the bot: ``!sama getAuthUrl``
    - Open the URL
    - You get asked if you want to connect your app with your reddit account -> allow
    - now you get redirected to something like ``http://localhost/?state=[A_RANDOM_ID]&code=[CODE]``
    - copy the ``[CODE]`` and sent a private message again: ``!sama authorize [CODE]``

Done! 

## Commands
There are 4 types of commands:
- **owner commands**: the owner of the discord bot can use these and all commands below
- **admin commands**: guild admins can use these and all commands below
- **vip commands**: users with specific roles can use these and all commands below
- **normal commands**: everyone can use them

## List of commands
Prefixes, commands and most of the output messages are configurable in the ``.properties`` files in
``src/main/resources`` directory

General shape of a command:
``prefix command <args...|'const values'...> [optional args]``

#### Owner commands
```
Signature:       !sama authorize <code>
Prefix/cmd keys: cmdOwnerPrefix cmdAuthorize
Description:     Gets the access- and refresh-token from reddit api.
                 <code>: see chapter Installation.
```
```
Signature:       !sama getAuthUrl
Prefix/cmd keys: cmdOwnerPrefix cmdGetAuthUrl
Description:     Returns the url you need to get the authorization code.
                 See chapter Installation.
```
```
Signature:       !sama goaway
Prefix/cmd keys: cmdOwnerPrefix cmdShutdown
Description:     Shuts down the bot and all running services, and the program terminates.
                 When you restart the program, all stopped services will start again
```

#### Admin commands
```
Signature:       !dono dwadd <r/subreddit> <'hot'|'new'|'rising'>
Prefix/cmd keys: cmdAdminPrefix cmdAddDownloader
Description:     Adds the subreddit to the downloader-list. Does nothing if subreddit already exists.
                 <r/subreddit>          an existing subreddit, has to start with 'r/'
                 <'hot'|'new'|'rising'> sort subreddit by hot|new|rising
```
```
Signature:       !dono vipadd <role ID>
Prefix/cmd keys: cmdAdminPrefix cmdAddVipRole
Description:     Adds a role to the vip-roles. these roles can execute all vip commands.
                 <role ID> a role
```
```
Signature:       !dono wtadd <r/subreddit> <'hot'|'new'|'rising'> [textChannel ID]
Prefix/cmd keys: cmdAdminPrefix cmdAddWatcher
Description:     Adds the subreddit to the watchlist. Does nothing if subreddit already exists.
                 <r/subreddit>          an existing subreddit, has to start with 'r/'
                 <'hot'|'new'|'rising'> sort subreddit by hot|new|rising
                 [textChannel ID]       optional: the ID of the textChannel where the bot prints
                                        out the reddit stuff. If not set, the channel where the 
                                        command got executed will be used
```
```
Signature:       !dono dwedit <r/subreddit> <'hot'|'new'|'rising'|'-'> <'rmID'|'-'>
Prefix/cmd keys: cmdAdminPrefix cmdEditDownloader
Description:     Edits an existing subreddit in downloadlist.
                 use '-' if you dont want to change a parameter
                 <r/subreddit>              an existing subreddit, has to start with 'r/'
                 <'hot'|'new'|'rising'|'-'> sorts subreddit by hot|new|rising
                 <'rmID'|->                 when 'rmID' is set, the bot starts by the 100th post
                                            from now instead of the last printed one
```
```
Signature:       !dono wtedit <r/subreddit> <'hot'|'new'|'rising'|'-'> <textChannel ID|'-'> <'rmID'|'-'>
Prefix/cmd keys: cmdAdminPrefix cmdEditWatcher
Description:     Edits an existing subreddit in watchlist. use '-' if you dont want to change a parameter
                 <r/subreddit>              an existing subreddit, has to start with 'r/'
                 <'hot'|'new'|'rising'|'-'> sorts subreddit by hot|new|rising
                 <textChannel ID|'-'>       sets a new channel where the reddit stuff gets printed
                 <'rmID'|->                 when 'rmID' is set, the bot starts by the 100th post from 
                                            now instead of the last printed one
```
```
Signature:       !dono dwremove <r/subreddit>
Prefix/cmd keys: cmdAdminPrefix cmdRemoveDownloader
Description:     Removes an existing subreddit from downloaderlist.
                 <r/subreddit> an existing subreddit, has to start with 'r/'
```
```
Signature:       !dono viprm <role ID|'all'>
Prefix/cmd keys: cmdAdminPrefix cmdRemoveVipRole
Description:     Removes a role from the vip-roles. these roles can execute all vip commands.
                 <role ID|'all'> a specific role, or all roles
```
```
Signature:       !dono wtremove <r/subreddit>
Prefix/cmd keys: cmdAdminPrefix cmdRemoveWatcher
Description:     Removes an existing subreddit from watchlist.
                 <r/subreddit> an existing subreddit, has to start with 'r/'
```

#### VIP commands (start/stop services)
```
Signature:       !san dwwatch <'start'|'stop'> <r/rubreddit|'all'>
                 !san dwwatch <'sync'>
Prefix/cmd keys: cmdVipPrefix cmdWatchDownloader
Description:     Starts/stops watching in subreddit for posts.
                 Multiple watchers starting with a 1-minute-delay
                 <'start'|'stop'>    starts/stops watching
                 <r/subreddit|'all'> an existing subreddit, has to start with 'r/', or just 'all'
                 <'sync'>            stops and restarts every active watcher
```
```
Signature:       !san wtwatch <'start'|'stop'> <r/rubreddit|'all'>
                 !san wtwatch <'sync'>
Prefix/cmd keys: cmdVipPrefix cmdWatchWatcher
Description:     Starts/stops downloading images from subreddit.
                 Multiple downloaders starting with a 1-minute-delay
                 <'start'|'stop'>    starts/stops downloading
                 <r/subreddit|'all'> an existing subreddit, has to start with 'r/', or just 'all'
                 <'sync'>            stops and restarts every active downloader
```

#### Normal commands
```
Signature:       !chan help [command]
Prefix/cmd keys: cmdGeneralPrefix cmdHelp
Description:     [command] optional: prints out the specific help
                 for the given command. If none is given, an overview 
                 with all available commands will be printed
```
```
Signature:       !chan random
Prefix/cmd keys: cmdGeneralPrefix cmdRandomPost
Description:     Gets a random post from a random subreddit
```
```
Signature:       !chan dwshow <r/subreddit|'all'>
Prefix/cmd keys: cmdGeneralPrefix cmdShowDownloaderList
Description:     Prints out all params of the given subreddit, 
                 or prints everything when 'all' is given
                 <r/subreddit|'all'> an existing subreddit, has to start with 'r/', or just 'all'
```
```
Signature:       !chan vipshow
Prefix/cmd keys: cmdGeneralPrefix cmdShowVipRole
Description:     Shows all vip-roles
```
```
Signature:       !chan wtshow <r/subreddit|'all'>
Prefix/cmd keys: cmdGeneralPrefix cmdShowWatchList
Description:     Prints out all params of the given subreddit,
                 or prints everything when 'all' is given
                 <r/subreddit|'all'> an existing subreddit, has to start with 'r/', or just 'all'
```