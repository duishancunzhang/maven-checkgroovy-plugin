import jodd.io.FileUtil
import org.apache.commons.io.FileUtils

def dirs = sourceDirectories
//def dirs = []
//dirs[0] = "E:\\ideaworkspace\\server-be\\microservice-framework\\microservice-test\\src\\main\\java"

for(String path:dirs){
    def sourceRoot = new File(path).getAbsolutePath()

    Collection<File> sources = FileUtils.listFiles(new File(sourceRoot), null, true)

    for (File file : sources) {
        String[] lines = FileUtil.readLines(file)
        int lineNum = 0
        for (String line : lines) {
            lineNum++

            // 包含@Valid 但不包含BindingResult
            if (line.contains("@Valid") && !line.contains("BindingResult")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 缺少BindingResult"
            }

            // 不包含@Valid 但包含BindingResult
            if (!line.contains("@Valid") && line.contains("BindingResult") && !line.contains("import")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 缺少@Valid"
            }

            // 缺少validateArgs
            if (line.contains("@Valid") && line.contains("BindingResult")) {
                if (!lines[lineNum].contains("validateArgs") && !lines[lineNum + 1].contains("validateArgs")) {
                    println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 缺少validateArgs"
                }
            }

            // Pager使用了Get请求
            if (line.toLowerCase().contains("pager")) {
                if (lines[lineNum - 2].toLowerCase().contains("@Get")) {
                    println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " Pager应该使用Post请求"
                }
            }

            // @Post缺少RequestBody
            if (line.contains("@Post")) {
                if (!lines[lineNum].contains("RequestBody") && !lines[lineNum + 1].contains("RequestBody")
                        && !lines[lineNum].contains("MultipartFile") && !lines[lineNum + 1].contains("MultipartFile")) {

                    println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 缺少RequestBody"
                }
            }

            // manager拼写问题
            if (line.contains("/manger")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " manager拼写为manger"
            }

            // manager拼写问题
            if (line.contains("/person") && !line.contains("/personal")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " personal拼写为person"
            }

            // System.out使用
            if (line.contains("System.out")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " System.out"
            }

            // v1使用
            if (line.contains("/v1")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 应该使用/v*而不是/v1"
            }

            // Protocol中不应该使用基本类型
            if (file.getName().contains("Protocol")) {
                if (line.contains(" bool ") || line.contains(" int ") || line.contains(" long ") || line.contains(" double ")) {
                    println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 不应该使用基本类型"
                }
            }

            // @EnableDiscoveryClient误被注释
            if (line.contains("//@EnableDiscoveryClient") || line.contains("*//**//**//**//*@EnableDiscoveryClient*//**//**//**//*")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 不能注释掉@EnableDiscoveryClient"
            }

            // Constant位置
            if (line.contains("static final") && line.contains("=")) {
                if (!file.name.contains("Message") && !file.name.contains("Constant")) {
                    println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 常量请定义在Constant中"
                }
            }

            // final参数
            if (line.contains("final") && line.contains("(")) {
                if (!file.name.contains("Message") && !file.name.contains("Constant")) {
                    println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 方法使用了final参数，看是不是不需要"
                }
            }

            // 单行if
            if (line.contains(" if ") && !line.contains("{")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 方法使用了单行if，不建议使用"
            }

            // getEnterpriseId
            if (line.contains("SessionUserUtil.getEnterpriseId") && !file.name.contains("Controller")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 请将获取企业id的由Service移到Controller"
            }

            // Result带泛型问题
            if (line.contains("public Result<") && !file.name.contains("Client")) {
                println "[WARN] [CHECKGROOVY] - "+ file.getName() +  " Line " + lineNum + " 返回的Result最好不要带泛型"
            }
        }
    }
}


