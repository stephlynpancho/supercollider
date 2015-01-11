
Git {

	*clone { |url, localPath|
		this.git([
			"clone",
			url,
			localPath.escapeChar($ )
		]);
	}
	*pull { |localPath|
		this.git(["pull"], localPath);
	}
	*checkout { |refspec, localPath|
		this.git(["checkout", refspec], localPath);
	}
	*isGit { |localPath|
		^File.exists(localPath +/+ ".git")
	}
	*isDirty { |localPath|
		var out = this.git(["--no-pager diff HEAD --"], localPath, true);
		if(out.size != 0, {
			out.debug;
			^true
		});
		^false
	}
	*remote { |localPath|
		// detect origin of repo or nil
		// origin	git://github.com/supercollider-quarks/MathLib (fetch)
		// origin	git://github.com/supercollider-quarks/MathLib (push)
		var out = this.git(["remote -v"], localPath, true),
			match = out.findRegexp("^origin\t([^ ]+) \\(fetch\\)");
		if(match.size > 0, {
			^match[1][1]
		});
		^nil
	}
	/*
	tag {
		git describe
		var out = GitQuarks.git(["log --pretty=format:'%d' --abbrev-commit --date=short -1"]);
		// match
		tag: 4.1.4,
	}
	*/
	*git { |args, cd, getOutput=false|
		var cmd;
		if(cd.notNil,{
			cmd = ["cd", cd.escapeChar($ ), "&&", "git"];
		},{
			cmd = ["git"];
		});
		cmd = (cmd ++ args).join(" ");
		if(getOutput, {
			// no way of knowing if there was an error
			^cmd.debug.unixCmdGetStdOut;
		});
		cmd.debug.unixCmd({ |result|
			if(result != 0, {
				("Git command failed [" ++ result ++ "]:" + cmd).error;
			})
		});
	}
}
