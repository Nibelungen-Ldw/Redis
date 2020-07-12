package com.atguigu.servlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atguigu.utils.VerifyCodeConfig;

import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class CodeSenderServlet
 */
@WebServlet("/CodeSenderServlet")
public class CodeSenderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CodeSenderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获得手机号
		String phone_no = request.getParameter("phone_no");
		// 获得验证码
		String code = getCode(VerifyCodeConfig.CODE_LEN);
		// 向Redis中进行存储键值对（键的设置不能单纯为手机号)
		// 拼接key
		String codeKey = VerifyCodeConfig.PHONE_PREFIX + phone_no + VerifyCodeConfig.CODE_SUFFIX;
		String countKey = VerifyCodeConfig.PHONE_PREFIX + phone_no + VerifyCodeConfig.COUNT_SUFFIX;

		Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT);

		// 判断当日发送验证码的次数是否超过三次
		String count = jedis.get(countKey);

		if (count == null) {
			// 第一次记录
			jedis.setex(countKey, VerifyCodeConfig.SECONDS_PER_DAY, "1");
		} else if (Integer.parseInt(count) <= VerifyCodeConfig.COUNT_TIMES_1DAY) {
			jedis.incr(countKey);
		} else if (Integer.parseInt(count) > VerifyCodeConfig.COUNT_TIMES_1DAY) {
			response.getWriter().print("limit");
			jedis.close();
			return;
		}
		jedis.setex(codeKey, VerifyCodeConfig.CODE_TIMEOUT, code);
		jedis.close();
		response.getWriter().print(true);

	}

	/**
	 * 
	 * @Description
	 * @author Nibelungen
	 * @date 2020年6月20日下午3:53:36
	 * @param length
	 * @return 返回手机验证码，随机生成
	 */
	private String getCode(int length) {
		String code = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			int rand = random.nextInt(10);
			code += rand;
		}
		return code;
	}

}
