//TODO: logging
//TODO: reaction roles

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.*;

import com.sun.prism.paint.Color;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Guild.*;
import net.dv8tion.jda.api.entities.MessageEmbed.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.message.*;
import net.dv8tion.jda.api.exceptions.*;
import net.dv8tion.jda.api.hooks.*;
public class Start extends ListenerAdapter {
	public static String[] BlackList;
	public static ArrayList<String> BL;
	public static ArrayList<String> BLR;
	public static String[] AdvisoryList;
	public static ArrayList<String> AL;
	public static ArrayList<String> ALR;
	public static ArrayList<String> AgeL;
	public static ArrayList<String> msged;
	public static ArrayList<String> blocked;
	static JDA jda;
	public static boolean memeMode = false;
	public static void main(String[] args) {
		try {
			fullReload();
			loadJDA();
			System.out.println("READY!");
			reload();
			System.out.println("READY!");
		} catch (LoginException | InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void loadJDA() throws LoginException, InterruptedException {
		jda = new JDABuilder(Token.token).addEventListeners(new Start()).addEventListeners(new BanBuddy()).setActivity(Activity.watching("DSC Member Servers")).setStatus(OnlineStatus.ONLINE).build();
		jda.awaitReady();
	}

	private static void fullReload() throws IOException {
		getBans();
		getAdvisories();
		getAges();
		getUsers();
		getVerified("block.DSC",blocked);
	}

	private static void reload() {
		for(Guild temp: jda.getGuilds()) {
			//	temp.getSelfMember().modifyNickname("DiscordScoutCouncil Bot").queue();
			List<Ban> bans = temp.retrieveBanList().complete();
			for(Ban temp2: bans) {
				addBan(temp2.getUser().getId(), temp2.getReason());
			}
			List<Member> memberlist = temp.getMembers();
			for(Member temp3: memberlist) {
				System.out.println("Checking member age of "+temp3.getAsMention());
				boolean ageVerified = false;
				for (String temp4: AgeL) {
					if (temp4.startsWith(temp3.getUser().getId())) {
						ageVerified = true;
					}
				}
				if (!ageVerified) {
					if (temp3.getUser().getId().equals("213319973756600322")) {
						if (!msged.contains(temp3.getUser().getId())) {
							msged.add(temp3.getUser().getId());
							addUser(temp3.getUser().getId());

							PrivateChannel pc = temp3.getUser().openPrivateChannel().complete();
							Message msg = pc.sendMessage("Howdy! You just joined a DSC member server (or already did and never received this message). DSC is a network of scouting servers which work together to provide a better and safer expirience for their members. As part of this mission, member servers enforce BSA's Youth Protection Guidelines, and to assist in doing so, many servers allow members to self identify as over or under 18. In an attempt to consodidate this, you may do this here by selecting - for under 18 or + for over 18.\nIf you don't receive a confirmation after selecting one, please type `\\\\age`").complete();
							pc.addReactionById(msg.getId(), "➕").queue();
							pc.addReactionById(msg.getId(), "➖").queue();
							pc.pinMessageById(msg.getId()).queue();
						}
					}
				}
			}
		}
		TextChannel DSC = jda.getTextChannelById("668964814684422184"); //For use in testing.
		MessageEmbed embed = new MessageEmbed(null, "Service Online!", "Shard: "+jda.getShardInfo().getShardId()+"/"+jda.getShardInfo().getShardTotal()+"\nPing: "+jda.getGatewayPing(), null, OffsetDateTime.now(), 0x33cc33, new Thumbnail("https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null, null);
		DSC.sendMessage(embed).queue();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		//These are provided with every event in JDA
		JDA jda = event.getJDA();                       //JDA, the core of the api.
		long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

		//Event specific information
		User author = event.getAuthor();                //The user that sent the message
		Message message = event.getMessage();           //The message that was received.
		MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.

		String msg = message.getContentDisplay();       


		if (event.isFromType(ChannelType.TEXT))
		{

			Guild guild = event.getGuild();             
			TextChannel textChannel = event.getTextChannel(); 
			Member member = event.getMember();      

			String name;
			if (message.isWebhookMessage())
			{
				name = author.getName();                
			}                                           
			else
			{
				name = member.getEffectiveName();      
			}                                           

			System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
		}
		else if (event.isFromType(ChannelType.PRIVATE)) 
		{
			System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
		}

		//Begin CMD parsing
		if (msg.equals("\\\\ping"))
		{
			channel.sendMessage("Bot Online. :)").queue();
		}
		else if (msg.toLowerCase().contains("bruh") && memeMode)
		{
			channel.sendMessage("stop.").queue();
		}
		else if (msg.toLowerCase().contains("scoot") && memeMode)
		{
			channel.sendMessage("stop.").queue();
		}
		else if (msg.toLowerCase().contains("oof") && memeMode)  
		{
			channel.sendMessage("stop.").queue();
		}
		else if (msg.toLowerCase().contains("neato") && memeMode)
		{
			channel.deleteMessageById(message.getId());
		}
		else if (msg.equals("\\\\age")) {
			msged.add(message.getAuthor().getId());
			addUser(message.getAuthor().getId());

			PrivateChannel pc = message.getAuthor().openPrivateChannel().complete();
			Message msg2 = pc.sendMessage("Howdy! You just joined a DSC member server (or already did and never received this message). DSC is a network of scouting servers which work together to provide a better and safer expirience for their members. As part of this mission, member servers enforce BSA's Youth Protection Guidelines, and to assist in doing so, many servers allow members to self identify as over or under 18. In an attempt to consodidate this, you may do this here by selecting - for under 18 or + for over 18.\nIf you don't receive a confirmation after selecting one, please type `\\\\age`").complete();
			pc.addReactionById(msg2.getId(), "➕").queue();
			pc.addReactionById(msg2.getId(), "➖").queue();
		}
		else if (msg.equals("\\\\verify")) {
			PrivateChannel pc = message.getAuthor().openPrivateChannel().complete();
			pc.sendMessage("**DSC Verification System:**").queue();
			pc.sendMessage("*Please read the directions before submitting.\nYou do not need to run `\\verify` again to submit more requests.*").queue();
			pc.sendMessage("To submit a request, upload a file here. The verification system will accept any file type, but only PDFs and images will be proccessed. PDFs may expirience some delays in processing, using images is prefered.\nPlease censor any personal information from on submitions.\nYour name does not have to be visible on any proof, although some servers prefer that a first or last name (but not both) is visible for verifying YPT. Type `\\verifyreqs` for information on what can be verified and what is need for each.").queue();
		}else if (msg.equals("\\\\verifyreqs")) {
			if (channel.getType().equals(ChannelType.PRIVATE)) {
				channel.sendMessage("For Eagle/Summit/Silver, a patch, knot, medal, certificate, or card will be accepted.\nFor camp staff, a shirt or official name tag will be accepted, if you would like to use an alternative means of verification, please ask a mod first.\nFor YPT, a card or certificate is acceptable.\nFor OA, a lodge flap, sash, card, or are acceptable. You may also submit a screenshot of your roles on SBSA, VBSA, or OA.").queue();
			}
		}
		else if (msg.equals("\\\\reload"))
		{
			if (message.getMember().getId().equals("213319973756600322")) {
				try {
					fullReload();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reload();
			} else {
				channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
			}
		}
		else if (msg.equals("\\\\performance")) 
		{
			String out = "";
			long r = jda.getResponseTotal();
			if (r/1000 < 1) {
				out = "error probability: none";
				out += "\n";
				for (int i = 0; i < r/100; i++) {
					out += "█";
				}
				for (int i = 10; i > r/100; i--) {
					out += "░";
				}
			} else if (r/10000 < 1) {
				out = "error probability: low";
				out += "\n";
				for (int i = 0; i < r/1000; i++) {
					out += "█";
				}
				for (int i = 10; i > r/1000; i--) {
					out += "░";
				}
			} else if (r/100000 < 1) {
				out = "error probability: medium";
				out += "\n";
				for (int i = 0; i < r/10000; i++) {
					out += "█";
				}
				for (int i = 10; i > r/10000; i--) {
					out += "░";
				}
			} else if (r/1000000 < 1) {
				out = "error probability: high";
				out += "\n";
				for (int i = 0; i < r/100000; i++) {
					out += "█";
				}
				for (int i = 10; i > r/100000; i--) {
					out += "░";
				}
			} else if (r/10000000 < 1) {
				out = "error probability: urgent";
				out += "\n";
				for (int i = 0; i < r/1000000; i++) {
					out += "█";
				}
				for (int i = 10; i > r/1000000; i--) {
					out += "░";
				}
			} else if (r/100000000 < 1) {
				out = "error probability: critical";
				out += "\n";
				for (int i = 0; i < r/10000000; i++) {
					out += "█";
				}
				for (int i = 10; i > r/10000000; i--) {
					out += "░";
				}
			} else {
				out = "Please Restart Now.";
			}
			channel.sendMessage(out).queue();
		}
		else if (msg.equals("\\\\syncbans")) 
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				channel.sendTyping().queue();
				List<Ban> bans = message.getGuild().retrieveBanList().complete();
				for(Ban temp: bans) {
					addBan(temp.getUser().getId(), temp.getReason());
				}
				channel.sendMessage("Finished syncing ban list to the DSC Blacklist.").queue();
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.contentEquals("\\\\help")) {
			List<Field> cmds = new ArrayList<Field>();
			cmds.add(new Field("\\\\advise <@user>", "Issues a warning about a user to all DSC member servers.", false));
			cmds.add(new Field("\\\\purge <#>", "Removes selected numer of messages.", false));
			cmds.add(new Field("\\\\raid", "Locks down server and alerts all DSC member servers.", false));
			cmds.add(new Field("\\\\clearBan", "Removes a user from the blacklist; User remains banned where already banned.", false));
			cmds.add(new Field("\\\\syncbans", "Sends contents of banlist to blacklist server.", false));
			cmds.add(new Field("\\\\checkbans", "Checks blacklist server for members in your server and warns you.", false));
			cmds.add(new Field("\\\\checkAdvisories", "Checks advisory list for members in your server and warns you.", false));
			cmds.add(new Field("\\\\runUser <tag>", "Information about a user, includes search of advisories and blacklist", false));
			cmds.add(new Field("\\\\setbanreason", "Change the reason for a ban.", false));
			cmds.add(new Field("\\\\setadvisoryreason", "Change the reason for an advisory.", false));
			cmds.add(new Field("On Ban", "Users are automatically added to the blaklist server when banned", false));
			cmds.add(new Field("On Join", "Users are automatically checked against the blacklist/advisory list when they join.", false));
			cmds.add(new Field("\\\\stats", "Mostly useless statistics about DSC and DSC bot.", false));
			cmds.add(new Field("\\\\serverstats", "Statistics about the server.", false));
			cmds.add(new Field("\\\\servers", "List of DSC servers with invite links.", false));
			MessageEmbed embed = new MessageEmbed(null, "About DSC and DSC Bot!", "Discord Scout Council, or DSC for short is a coalition of scouting servers which work together to improve their servers and keep members safe. Have a server and want to join? Contact a mod of a member server and they can bring your server up for consideration. DSC Bot is exclusive to DSC member servers and offers member protections such as raid mode and an optional shared ban list.", null, null, 0, new Thumbnail("https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot 3.0.0r Build Date: 3/5/2020| Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null,cmds);
			channel.sendMessage(embed).queue();
			channel.sendMessage("This menu is being reworked, please be patient with us. :)").queue();
		}
		else if (msg.equals("\\\\checkbans"))
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().hasPermission(Permission.BAN_MEMBERS)) {
					channel.sendTyping().queue();
					for(Member bantemp: message.getGuild().getMembers()) {
						if(BL.contains(bantemp.getId())) {
							channel.sendMessage("Found: "+bantemp.getAsMention()+" For "+BLR.get(BL.indexOf(bantemp.getId()))).queue();
						}
					}
					channel.sendMessage("DSC Blacklist Check Finished!").queue();
				}
				else
				{
					channel.sendMessage("You are not authorized to do this on this server. [BAN_MEMBERS permission required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\setbanreason"))
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().hasPermission(Permission.BAN_MEMBERS)) {

					String id = msg.split(" ")[1];
					msg = msg.substring(13+id.length());

					addBan(id,msg);

					channel.sendMessage("Reason Updated!").queue();
				}
				else
				{
					channel.sendMessage("You are not authorized to do this on this server. [BAN_MEMBERS permission required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\setadvisoryreason"))
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().hasPermission(Permission.KICK_MEMBERS)) {

					String id = msg.split(" ")[1];
					msg = msg.substring(13+id.length());

					addAdvisory(id,msg);

					channel.sendMessage("Reason Updated!").queue();
				}
				else
				{
					channel.sendMessage("You are not authorized to do this on this server. [KICK_MEMBERS permission required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.equals("\\\\checkAdvisories"))
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().hasPermission(Permission.KICK_MEMBERS)) {
					channel.sendTyping().queue();
					for(Member bantemp: message.getGuild().getMembers()) {
						if(AL.contains(bantemp.getId())) {
							channel.sendMessage("Found: "+bantemp.getAsMention()+" For "+ALR.get(AL.indexOf(bantemp.getId()))).queue();
						}
					}
					channel.sendMessage("DSC Advisory Check Finished!").queue();
				}
				else
				{
					channel.sendMessage("You are not authorized to do this on this server. [KICK_MEMBERS permission required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\clearBan"))
		{
			if (message.getMember().getId().equals("213319973756600322")) {
				List<User> mentionedUsers = message.getMentionedUsers();
				for (User tempbanuser : mentionedUsers) {
					if(BL.contains(tempbanuser.getId())) {
						removeBan(tempbanuser.getId());
						try {
							getBans();
						} catch (IOException e) {
							e.printStackTrace();
						}
						channel.sendMessage("Cleared "+tempbanuser.getAsMention()).queue();
					}
				}
			} else {
				channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
			}

		}
		else if (msg.startsWith("\\\\clearBlock"))
		{
			if (message.getMember().getId().equals("213319973756600322")) {
				List<User> mentionedUsers = message.getMentionedUsers();
				for (User tempbanuser : mentionedUsers) {
					if(blocked.contains(tempbanuser.getId())) {
						removeVerified(tempbanuser.getId(),"block.DSC",blocked);
						channel.sendMessage("Cleared "+tempbanuser.getAsMention()).queue();
					}
				}
			} else {
				channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
			}
		}else if (msg.startsWith("\\\\unverify"))
		{
			if (message.getMember().getId().equals("213319973756600322")) {
				String[] args = msg.split(" ");
				String id = args[1];
				Member usr = message.getGuild().getMemberById(id);
				removeVerified(id,args[2],new ArrayList<String>());
				channel.sendMessage("Unverified "+usr.getAsMention()).queue();
			} else {
				channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
			}
		}
		else if (msg.startsWith("\\\\clearBanID"))
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().getId().equals("213319973756600322")) {
					String tempbanuser = msg.split(" ")[1];
					if(BL.contains(tempbanuser)) {
						removeBan(tempbanuser);
						try {
							getBans();
						} catch (IOException e) {
							e.printStackTrace();
						}
						channel.sendMessage("Cleared User ID \""+tempbanuser+"\"").queue();
					}
				} else {
					channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\clearAdvisory"))
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().getId().equals("213319973756600322")) {
					List<User> mentionedUsers = message.getMentionedUsers();
					for (User tempbanuser : mentionedUsers) {
						if(AL.contains(tempbanuser.getId())) {
							removeAdvisory(tempbanuser.getId());
							try {
								getAdvisories();
							} catch (IOException e) {
								e.printStackTrace();
							}
							channel.sendMessage("Cleared "+tempbanuser.getAsMention()).queue();
						}
					}
				} else {
					channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\clearAdvisoryID"))
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().getId().equals("213319973756600322")) {
					String tempbanuser = msg.split(" ")[1];
					if(AL.contains(tempbanuser)) {
						removeAdvisory(tempbanuser);
						try {
							getAdvisories();
						} catch (IOException e) {
							e.printStackTrace();
						}
						channel.sendMessage("Cleared User ID \""+tempbanuser+"\"").queue();
					}
				} else {
					channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\activateMemeMode"))
		{
			if (message.getMember().getId().equals("213319973756600322")) {
				memeMode = true;
				channel.sendMessage("Let the chaos begin. :)").queue();
			} else {
				channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
			}
		}
		else if (msg.startsWith("\\\\iHazRegerts"))   
		{
			if (message.getMember().getId().equals("213319973756600322")) {
				memeMode = false;
				channel.sendMessage("Problem Solved?").queue();
			} else {
				channel.sendMessage("You are not authorized to do this on this server. [Tfinnm#8609 required]").queue();
			}
		}
		else if (msg.startsWith("\\\\purge"))   //Note, I used "startsWith, not equals.
		{
			if (message.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
				int num = Integer.parseInt(msg.split(" ")[1]);
				num++;
				int times = num/100;
				int remain = num%100;
				List<Message> msgs = new MessageHistory(channel).retrievePast(remain).complete();
				channel.purgeMessages(msgs);

				for (int i = 0; i < times; i++) {
					List<Message> msgs2 = new MessageHistory(channel).retrievePast(100).complete();
					channel.purgeMessages(msgs2);
				}
			} else {
				channel.sendMessage("You are not authorized to do this on this server. [MESSAGE_MANAGE permission required]").queue();
			}
		}
		else if (msg.contentEquals("\\\\stats")) {
			List<Field> cmds = new ArrayList<Field>();
			cmds.add(new Field("Servers", String.valueOf(jda.getGuilds().size()), false));
			int totalMembers = 0;
			int totalChannels = 0;
			int totalBoosts = 0;
			//int totalMods = 0;
			for (Guild temp: jda.getGuilds()) {
				totalMembers += temp.getMemberCount();
				totalChannels += (temp.getTextChannels().size()+temp.getVoiceChannels().size());
				totalBoosts += temp.getBoostCount();
				//totalMods += temp.getMembersWithRoles(temp.getRolesByName("mod", true)).size();
			}
			cmds.add(new Field("Members", String.valueOf(totalMembers), true));
			cmds.add(new Field("Unique Users", String.valueOf(msged.size()), true));
			cmds.add(new Field("Channels", String.valueOf(totalChannels), true));
			cmds.add(new Field("Boosts", String.valueOf(totalBoosts), true));
			//cmds.add(new Field("\"Mods\"", String.valueOf(totalMods), true));
			cmds.add(new Field("Synced Bans", String.valueOf(BL.size()), true));
			cmds.add(new Field("Active Advisories", String.valueOf(AL.size()), true));
			cmds.add(new Field("Ping", String.valueOf(jda.getGatewayPing()), true));
			cmds.add(new Field("Shard", jda.getShardInfo().getShardString(), true));
			cmds.add(new Field("Uptime Events", String.valueOf(responseNumber),true));
			MessageEmbed embed = new MessageEmbed(null, "Random DSC Bot Statistics", "", null, null, 0, new Thumbnail("https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null,cmds);
			channel.sendMessage(embed).queue();
		}
		else if (msg.contentEquals("\\\\serverstats")) {
			List<Field> cmds = new ArrayList<Field>();
			Guild curserver = message.getGuild();
			cmds.add(new Field("Name", curserver.getName(), true));
			cmds.add(new Field("Owner", curserver.getOwner().getEffectiveName(), true));
			cmds.add(new Field("Created", String.valueOf(curserver.getTimeCreated()), true));
			cmds.add(new Field("Boosts", String.valueOf(curserver.getBoostCount()), true));
			cmds.add(new Field("Users", String.valueOf(curserver.getMemberCount()), true));
			cmds.add(new Field("Roles", String.valueOf(curserver.getRoles().size()), true));
			cmds.add(new Field("Channels", String.valueOf(curserver.getChannels().size()), true));
			cmds.add(new Field("Region", curserver.getRegion().getName(),true));
			MessageEmbed embed = new MessageEmbed(null, "Server Statistics", "", null, null, 0, new Thumbnail(message.getGuild().getIconUrl(), null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null,cmds);
			channel.sendMessage(embed).queue();
		}
		else if (msg.startsWith("\\\\runUser")) {
			List<Field> cmds = new ArrayList<Field>();
			List<User> mentionedUsers = message.getMentionedUsers();
			for (User curruser : mentionedUsers) {
				cmds.add(new Field("Name", curruser.getAsTag(), true));
				cmds.add(new Field("ID", curruser.getId(), true));
				cmds.add(new Field("Joined Discord", String.valueOf(curruser.getTimeCreated()), true));
				cmds.add(new Field("Joined Server", String.valueOf(message.getGuild().getMemberById(curruser.getId()).getTimeJoined()), true));
				String ypt = "";
				try {
					if (getVerified("YPT.DSC",new ArrayList<String>()).contains(curruser.getId())) {
						ypt+="YPT Certified\n";
					}

					if (AgeL.contains(curruser.getId()+"+")) {
						ypt+="Over 18";
					} else if (AgeL.contains(curruser.getId()+"-")) {
						ypt+="Under 18";
					} else {
						ypt+="Age Unknown";
					}
					cmds.add(new Field("YPT",ypt,true));
					String awards = "";
					if (getVerified("eagle.DSC",new ArrayList<String>()).contains(curruser.getId())) {
						awards+="Eagle\n";
					}
					if (getVerified("summit.DSC",new ArrayList<String>()).contains(curruser.getId())) {
						awards+="Summit\n";
					}
					if (getVerified("vigil.DSC",new ArrayList<String>()).contains(curruser.getId())) {
						awards+="OA Vigil\n";
					} else if (getVerified("brotherhood.DSC",new ArrayList<String>()).contains(curruser.getId())) {
						awards+="OA Brotherhood\n";
					}else if (getVerified("ordeal.DSC",new ArrayList<String>()).contains(curruser.getId())) {
						awards+="OA Ordeal\n";
					}
					if (getVerified("Camp Staff.DSC",new ArrayList<String>()).contains(curruser.getId())) {
						awards+="Camp Staff";
					}

					cmds.add(new Field("Verified Roles",awards,true));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String roles = "";
				for(Role temprole: message.getGuild().getMemberById(curruser.getId()).getRoles()) {
					roles += temprole.getAsMention();
				}
				cmds.add(new Field("Roles", roles, true));
				String servers = "";
				for(Guild tempguild: curruser.getMutualGuilds()) {
					servers += tempguild.getName() + "\n";
				}
				cmds.add(new Field("DSC Servers", servers,true));
				int color = 0x33cc33; 
				String warnmsg = "User is in good standing with DSC.";
				if (memeMode) {
					warnmsg = "User is guilty of warcrimes for eating pizza on pineapple.";
				}
				if (BL.contains(curruser.getId())) {
					color = 0xF40C0C;
					warnmsg =  "User has a current ban on a DSC member server for "+BLR.get(BL.indexOf(curruser.getId()));
				} else if (AL.contains(curruser.getId())) {
					color = 0xd4af37;
					warnmsg =  "User has an advisory on a DSC member server for "+ALR.get(AL.indexOf(curruser.getId()));
				}
				MessageEmbed embed = new MessageEmbed(null, "User Info", warnmsg, null, null, color, new Thumbnail(curruser.getEffectiveAvatarUrl(), null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null,cmds);
				channel.sendMessage(embed).queue();
			}
		}
		else if (msg.contentEquals("\\\\servers")) {
			List<Field> cmds = new ArrayList<Field>();
			for(Guild tempguild: jda.getGuilds()) {
				if ((!tempguild.getId().equals("646540745443901469"))&&(!tempguild.getId().equals("634847034930626561"))) {
					cmds.add(new Field(tempguild.getName(), "Owner: "+tempguild.getOwner().getAsMention()+"\nInvite: "+tempguild.getDefaultChannel().createInvite().complete().getUrl(), false));
				}
			}
			MessageEmbed embed = new MessageEmbed(null, "DSC Member Servers", "Run a server that's intrested in joining? Contact a mod of any of these servers for more information.", null, null, 0, new Thumbnail("https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null,cmds);
			channel.sendMessage(embed).queue();
		}
		else if (msg.startsWith("\\\\advise"))   //Note, I used "startsWith, not equals.
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().hasPermission(Permission.KICK_MEMBERS)) {
					//If no users are provided, we can't kick anyone!
					if (message.getMentionedUsers().isEmpty())
					{
						channel.sendMessage("You must mention a user to issue an advisory for!").queue();
					}
					else
					{
						Guild guild = event.getGuild();
						//Member selfMember = guild.getSelfMember();  //This is the currently logged in account's Member object.
						// Very similar to JDA#getSelfUser()!

						List<User> mentionedUsers = message.getMentionedUsers();
						for (User user : mentionedUsers)
						{
							//Member member = guild.getMember(user);  //We get the member object for each mentioned user to kick them!
							addAdvisory(user.getId(),message.getContentRaw().substring(7+user.getId().length()+5));
							channel.sendMessage("Issued Advisory.").queue();

							TextChannel DSC = jda.getTextChannelById("646540745443901472"); //For use in production.
							//TextChannel DSC = jda.getTextChannelById("668964814684422184"); //For use in testing.
							MessageEmbed embed = new MessageEmbed(null, "ADVISORY!", "User: "+user.getAsTag()+"\nID: "+user.getId()+"\nServer: "+guild.getName()+"\nFor Reason: "+message.getContentRaw().substring(7+user.getId().length()+5), null, OffsetDateTime.now(), 0xF40C0C, new Thumbnail(user.getEffectiveAvatarUrl(), null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null, null);
							DSC.sendMessage(embed).queue();

						}
					}
				}
				else
				{
					channel.sendMessage("You are not authorized to do this on this server. [KICK_MEMBERS permission required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\raid"))   //Note, I used "startsWith, not equals.
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().hasPermission(Permission.KICK_MEMBERS)) {
					//If no users are provided, we can't kick anyone!
					Guild guild = event.getGuild();

					channel.sendMessage("Activating Raid Mode.").queue();

					for(GuildChannel temp: guild.getChannels()) {
						if (temp.getType().equals(ChannelType.TEXT)) {
							temp.getManager().setSlowmode(21600).queue();
						}
					}

					TextChannel DSC = jda.getTextChannelById("646541298622267402"); //For use in production.
					//TextChannel DSC = jda.getTextChannelById("668964814684422184"); //For use in testing.
					DSC.sendMessage("@everyone").queue();
					MessageEmbed embed = new MessageEmbed(null, "RAID IN PROGRESS!", "Server: "+guild.getName(), null, OffsetDateTime.now(), 0xF40C0C, new Thumbnail(guild.getIconUrl(), null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null, null);
					DSC.sendMessage(embed).queue();

				}
				else
				{
					channel.sendMessage("You are not authorized to do this on this server. [KICK_MEMBERS permission required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}
		else if (msg.startsWith("\\\\unraid"))   //Note, I used "startsWith, not equals.
		{
			if (message.isFromType(ChannelType.TEXT))
			{
				if (message.getMember().hasPermission(Permission.ADMINISTRATOR)) {
					//If no users are provided, we can't kick anyone!
					Guild guild = event.getGuild();

					channel.sendMessage("Disabling Raid Mode.").queue();

					for(GuildChannel temp: guild.getChannels()) {
						if (temp.getType().equals(ChannelType.TEXT)) {
							temp.getManager().setSlowmode(0).queue();
						}
					}

				}
				else
				{
					channel.sendMessage("You are not authorized to do this on this server. [ADMINISTRATOR permission required]").queue();
				}
			}
			else
			{
				channel.sendMessage("This is a Guild-Only command!").queue();
			}
		}

	}

	static void getBans() throws IOException {
		File file = new File("BL.DSC"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 
		BL = new ArrayList<String>();
		BLR = new ArrayList<String>();

		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			String st2 = "";
			if (st.split("\\|").length > 1) {
				st2 = st.split("\\|")[1];
			}
			st = st.split("\\|")[0];
			if (st2 == null) {
				st2 = "";
			}
			BL.add(st);
			BLR.add(st2);
			BlackList = new String[BL.size()];
			for (int i = 0; i < BL.size(); i++) {
				System.out.println(BL.get(i));
				BlackList[i] = BL.get(i);
				System.out.println(BlackList[i]);
			}
		}
		br.close();
	}

	public static void addBan(String ID,String reason) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader("BL.DSC"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.startsWith(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			inputBuffer.append(ID+"|"+reason);
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("BL.DSC");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	public static void removeBan(String ID) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader("BL.DSC"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.startsWith(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("BL.DSC");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}


	static void getAdvisories() throws IOException {
		File file = new File("AL.DSC"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 
		AL = new ArrayList<String>();
		ALR = new ArrayList<String>();

		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			String st2 = st.split("\\|")[1];
			st = st.split("\\|")[0];
			AL.add(st);
			ALR.add(st2);
			AdvisoryList = new String[AL.size()];
			for (int i = 0; i < AL.size(); i++) {
				System.out.println(AL.get(i));
				AdvisoryList[i] = AL.get(i);
				System.out.println(AdvisoryList[i]);
			}
		}
		br.close();
	}

	public static void addAdvisory(String ID,String reason) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader("AL.DSC"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.startsWith(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			inputBuffer.append(ID+"|"+reason);
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("AL.DSC");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	public static void setAge(String ID,String s) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader("age.DSC"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.startsWith(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			inputBuffer.append(ID+s);
			AgeL.add(ID+s);
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("age.DSC");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	public static void removeAdvisory(String ID) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader("AL.DSC"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.startsWith(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("AL.DSC");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	static void getAges() throws IOException {
		File file = new File("age.DSC"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 
		AgeL = new ArrayList<String>();
		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			AgeL.add(st);
		}
		br.close();
	}
	static void getUsers() throws IOException {
		File file = new File("users.DSC"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 
		msged = new ArrayList<String>();
		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			msged.add(st);
		}
		br.close();
	}

	public static void addUser(String ID) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader("users.DSC"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.equals(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			inputBuffer.append(ID);
			AgeL.add(ID);
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("users.DSC");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	public static void verify(String ID,String f,ArrayList<String> blocked2) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader(f));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.equals(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			inputBuffer.append(ID);
			blocked2.add(ID);
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream(f);
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	static ArrayList<String> getVerified(String f, ArrayList<String> arr) throws IOException {
		File file = new File(f); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 
		arr = new ArrayList<String>();
		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			arr.add(st);
		}
		br.close();
		return arr;
	}

	public static void removeVerified(String ID, String f, ArrayList<String> arr) {
		try {//646545388576178178
			BufferedReader file = new BufferedReader(new FileReader(f));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.startsWith(ID)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
			}
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream(f);
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

			arr.remove(ID);

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}




}
