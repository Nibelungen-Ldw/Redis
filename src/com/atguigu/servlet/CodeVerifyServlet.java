package com.atguigu.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atguigu.utils.VerifyCodeConfig;

import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class CodeVerifyServlet
 */
@WebServlet("/CodeVerifyServlet")
public class CodeVerifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CodeVerifyServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取验证码和手机号
		String phone_no = request.getParameter("phone_no");
		String verify_code = request.getParameter("verify_code");
		// 从Redis中获取手机号对应的验证码
		String codeKey = VerifyCodeConfig.PHONE_PREFIX + phone_no + VerifyCodeConfig.CODE_SUFFIX;
		Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT);
		String code = jedis.get(codeKey);
		if (code.equals(verify_code)) {
			response.getWriter().print(true);
		}
		jedis.close();

	}

}
